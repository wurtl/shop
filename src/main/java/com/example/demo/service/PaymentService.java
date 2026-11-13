package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Имитация платёжного шлюза: вместо запроса к API банка работаем с записью в БД. */
@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    // Репозиторий платежей и сервис заказов внедряются через конструктор
    public PaymentService(PaymentRepository paymentRepository, OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    /**
     * Создать платёж по заказу.
     * Заказ должен принадлежать покупателю и находиться в статусе «Ожидает оплаты».
     * Сумма берётся из заказа, а не от клиента — так её нельзя подменить.
     * Повторный вызов возвращает уже созданный платёж, дублей не будет.
     */
    public Payment createPayment(User customer, Long orderId) {
        Order order = orderService.getCustomerOrder(customer, orderId);
        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT) {
            throw new BusinessException("Заказ не ожидает оплаты");
        }
        return paymentRepository.findByOrderId(orderId).orElseGet(() -> {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setAmount(order.getTotalPrice());
            payment.setPaymentUrl("https://pay.example.com/session/" + System.currentTimeMillis());
            return paymentRepository.save(payment);
        });
    }

    /**
     * Платёж по ID с проверкой, что заказ принадлежит этому покупателю.
     */
    @Transactional(readOnly = true)
    public Payment getPayment(User customer, Long paymentId) {
        Payment payment = paymentRepository.getOrThrow(paymentId);
        orderService.getCustomerOrder(customer, payment.getOrder().getId()); // проверка владельца
        return payment;
    }

    /**
     * «Банк подтвердил оплату»: заказ переходит в «Оплачен», платёж — в SUCCESS.
     * Повторное подтверждение уже оплаченного платежа ничего не меняет.
     */
    public Payment confirm(User customer, Long paymentId) {
        Payment payment = getPayment(customer, paymentId);
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return payment;
        }
        orderService.markPaid(payment.getOrder());
        payment.setStatus(PaymentStatus.SUCCESS);
        return paymentRepository.save(payment);
    }
}
