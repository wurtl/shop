package com.example.demo.repository;

import com.example.demo.entity.CartItem;

import java.util.List;
import java.util.Optional;

/**
 * Позиции корзины. Запросы строятся Spring Data по именам методов.
 */
public interface CartItemRepository extends BaseRepository<CartItem> {
    // Вся корзина пользователя
    List<CartItem> findByCustomerId(Long customerId);

    // Конкретный товар в корзине (чтобы обновить количество, а не дублировать)
    Optional<CartItem> findByCustomerIdAndProductId(Long customerId, Long productId);

    // Очистить корзину полностью (после оформления заказа)
    void deleteByCustomerId(Long customerId);

    @Override
    default String notFoundMessage() { return "Позиция корзины не найдена"; }
}
