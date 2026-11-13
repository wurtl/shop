package com.example.demo.service;

import com.example.demo.DTO.CreateOrderRequest;
import com.example.demo.entity.*;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.Require;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Заказы. Все смены статуса идут через {@link #changeStatus}, а допустимость перехода
 * проверяет {@link Order#moveTo}.
 *
 * AWAITING_PAYMENT → PAID → ACCEPTED → ASSEMBLED → DELIVERING → DELIVERED
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    // Репозитории и сервис корзины внедряются через конструктор
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    // --- Покупатель ---

    /**
     * Оформить заказ из корзины покупателя.
     * Адрес, текст открытки и время доставки берутся из запроса; сумма — это сумма позиций корзины.
     * Для каждой позиции корзины создаётся позиция заказа с зафиксированной ценой на момент покупки,
     * после чего корзина очищается. Новый заказ всегда в статусе «Ожидает оплаты».
     * Товары сразу списываются с остатка (резервируются под заказ); если хоть одного не хватает,
     * заказ не создаётся и ничего не списывается. Если корзина пуста — ошибка 400.
     */
    public Order createOrder(User customer, CreateOrderRequest request) {
        Require.notBlank(request.address(), "Не указан адрес доставки");
        List<CartItem> cart = cartService.getCart(customer);
        if (cart.isEmpty()) {
            throw new BusinessException("Корзина пуста");
        }

        reserveStock(cart);

        Order order = new Order();
        order.setCustomer(customer);
        order.setAddress(request.address());
        order.setCardText(request.cardText());
        order.setDeliverySlot(request.deliverySlot());
        order.setTotalPrice(cart.stream().map(CartItem::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        Order saved = orderRepository.save(order);

        orderItemRepository.saveAll(cart.stream().map(item -> OrderItem.from(saved, item)).toList());
        cartService.clearCart(customer);
        return saved;
    }

    /**
     * История заказов покупателя, сначала новые.
     */
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(User customer) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
    }

    /**
     * Заказ по ID с проверкой владельца.
     * Чужой заказ для покупателя «не существует» (404), чтобы нельзя было перебором узнать, какие ID заняты.
     */
    @Transactional(readOnly = true)
    public Order getCustomerOrder(User customer, Long id) {
        Order order = orderRepository.getOrThrow(id);
        if (!order.isOwnedBy(customer)) {
            throw new NotFoundException(orderRepository.notFoundMessage());
        }
        return order;
    }

    /**
     * Отмена заказа покупателем.
     * Допустима, пока флорист не принял заказ (допустимость перехода проверяет Order.moveTo).
     * Товары из отменённого заказа возвращаются на остаток.
     */
    public Order cancelOrder(User customer, Long id) {
        Order order = changeStatus(getCustomerOrder(customer, id), OrderStatus.CANCELLED);
        releaseStock(order);
        return order;
    }

    /**
     * Вызывается после подтверждения платежа: «Ожидает оплаты» → «Оплачен».
     */
    public Order markPaid(Order order) {
        return changeStatus(order, OrderStatus.PAID);
    }

    // --- Флорист ---

    /**
     * Заказ по ID без проверки владельца — для сотрудников (флориста и курьера).
     */
    @Transactional(readOnly = true)
    public Order getOrder(Long id) {
        return orderRepository.getOrThrow(id);
    }

    /**
     * Новые оплаченные заказы, которые ждут флориста (сначала с ближайшей доставкой).
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersForAssembly() {
        return orderRepository.findByStatusOrderByDeliverySlotAsc(OrderStatus.PAID);
    }

    /**
     * Заказы, закреплённые за флористом, кроме отменённых.
     */
    @Transactional(readOnly = true)
    public List<Order> getFloristOrders(User florist) {
        return orderRepository.findByFloristIdAndStatusNot(florist.getId(), OrderStatus.CANCELLED);
    }

    /**
     * «Оплачен» → «Принят»: флорист берёт заказ в работу и закрепляется за ним.
     */
    public Order accept(User florist, Long orderId) {
        Order order = getOrder(orderId);
        order.setFlorist(florist); // при недопустимом переходе транзакция откатится
        return changeStatus(order, OrderStatus.ACCEPTED);
    }

    /**
     * «Принят» → «Собран»: заказ готов к передаче курьеру. Завершить сборку может только флорист, принявший заказ.
     */
    public Order completeAssembly(User florist, Long orderId) {
        Order order = getOrder(orderId);
        requireAssignee(order.getFlorist(), florist);
        return changeStatus(order, OrderStatus.ASSEMBLED);
    }

    // --- Курьер ---

    /**
     * Собранные заказы, которые ждут курьера (сначала с ближайшей доставкой).
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersForDelivery() {
        return orderRepository.findByStatusOrderByDeliverySlotAsc(OrderStatus.ASSEMBLED);
    }

    /**
     * Заказы, которые взял курьер.
     */
    @Transactional(readOnly = true)
    public List<Order> getCourierOrders(User courier) {
        return orderRepository.findByCourierId(courier.getId());
    }

    /**
     * «Собран» → «Доставляется»: курьер забирает заказ и закрепляется за ним.
     */
    public Order startDelivery(User courier, Long orderId) {
        Order order = getOrder(orderId);
        order.setCourier(courier); // при недопустимом переходе транзакция откатится
        return changeStatus(order, OrderStatus.DELIVERING);
    }

    /**
     * «Доставляется» → «Доставлен»: заказ вручен покупателю. Завершить доставку может только курьер, взявший заказ.
     */
    public Order completeDelivery(User courier, Long orderId) {
        Order order = getOrder(orderId);
        requireAssignee(order.getCourier(), courier);
        return changeStatus(order, OrderStatus.DELIVERED);
    }

    /**
     * Списать с остатка товары из корзины. Каждый товар списывается атомарным запросом к базе
     *  поэтому при гонке двух покупателей остаток не станет отрицательным.
     * Товары обрабатываются в порядке ID — так параллельные заказы не блокируют друг друга взаимно.
     * Если какого-то товара не хватило, бросается BusinessException и вся транзакция откатывается,
     * включая уже выполненные списания.
     */
    private void reserveStock(List<CartItem> cart) {
        cart.stream()
                .sorted(Comparator.comparing(item -> item.getProduct().getId()))
                .forEach(item -> {
                    int updated = productRepository.decreaseStock(item.getProduct().getId(), item.getQuantity());
                    if (updated == 0) {
                        throw new BusinessException("Недостаточно товара «%s» на складе"
                                .formatted(item.getProduct().getName()));
                    }
                });
    }

    /**
     * Вернуть на остаток товары из отменённого заказа (количества берутся из позиций заказа).
     */
    private void releaseStock(Order order) {
        orderItemRepository.findByOrderId(order.getId())
                .forEach(item -> productRepository.increaseStock(item.getProduct().getId(), item.getQuantity()));
    }

    /**
     * Проверка исполнителя: заказ, закреплённый за одним сотрудником, другой завершить не может.
     * Если исполнитель ещё не назначен (null), проверку пропускаем — недопустимость шага тогда выявит moveTo.
     */
    private void requireAssignee(User assignee, User actor) {
        if (assignee != null && !assignee.getId().equals(actor.getId())) {
            throw new BusinessException("Заказ закреплён за другим сотрудником");
        }
    }

    /**
     * Единая точка смены статуса: Order.moveTo проверяет допустимость перехода
     * (иначе BusinessException), затем заказ сохраняется.
     */
    private Order changeStatus(Order order, OrderStatus next) {
        order.moveTo(next);
        return orderRepository.save(order);
    }
}
