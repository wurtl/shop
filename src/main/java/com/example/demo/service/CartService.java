package com.example.demo.service;

import com.example.demo.DTO.CartItemRequest;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.exception.Require;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CartService {

    private static final String QUANTITY_MESSAGE = "Количество должно быть больше нуля";

    private final CartItemRepository cartRepository;
    private final ProductRepository productRepository;

    // Репозитории корзины и товаров внедряются через конструктор
    public CartService(CartItemRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    /**
     * Содержимое корзины пользователя.
     */
    @Transactional(readOnly = true)
    public List<CartItem> getCart(User user) {
        return cartRepository.findByCustomerId(user.getId());
    }

    /**
     * Добавить товар в корзину.
     * Если такой товар уже есть — увеличиваем количество, иначе создаём новую позицию.
     * Количество по умолчанию — 1.
     */
    public CartItem addItem(User user, CartItemRequest request) {
        Product product = productRepository.getOrThrow(request.productId());
        Require.positive(request.quantityOrDefault(), QUANTITY_MESSAGE);

        CartItem item = cartRepository.findByCustomerIdAndProductId(user.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCustomer(user);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int newQuantity = item.getQuantity() + request.quantityOrDefault();
        requireInStock(product, newQuantity);
        item.setQuantity(newQuantity);
        return cartRepository.save(item);
    }

    /**
     * Установить новое количество позиции. Чужая позиция — 404.
     */
    public CartItem updateQuantity(User user, Long itemId, Integer quantity) {
        Require.positive(quantity, QUANTITY_MESSAGE);
        CartItem item = getOwnedItem(user, itemId);
        requireInStock(item.getProduct(), quantity);
        item.setQuantity(quantity);
        return cartRepository.save(item);
    }

    /**
     * Удалить одну позицию из корзины. Чужая позиция — 404.
     */
    public void removeItem(User user, Long itemId) {
        cartRepository.delete(getOwnedItem(user, itemId));
    }

    /**
     * Очистить корзину целиком (вызывается и после оформления заказа).
     */
    public void clearCart(User user) {
        cartRepository.deleteByCustomerId(user.getId());
    }

    /**
     * Нельзя положить в корзину больше, чем есть на складе (общее количество этого товара в корзине).
     * Это ранняя проверка для удобства покупателя; окончательно остаток проверяется при оформлении заказа.
     */
    private void requireInStock(Product product, int quantity) {
        int available = Objects.requireNonNullElse(product.getStock(), 0);
        if (quantity > available) {
            throw new BusinessException("Недостаточно товара «%s»: в наличии %d".formatted(product.getName(), available));
        }
    }

    /**
     * Найти позицию корзины и проверить владельца: чужая позиция для пользователя «не существует».
     */
    private CartItem getOwnedItem(User user, Long itemId) {
        CartItem item = cartRepository.getOrThrow(itemId);
        if (!item.isOwnedBy(user)) {
            throw new NotFoundException(cartRepository.notFoundMessage());
        }
        return item;
    }
}
