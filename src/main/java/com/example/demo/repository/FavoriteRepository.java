package com.example.demo.repository;

import com.example.demo.entity.Favorite;

import java.util.List;
import java.util.Optional;

/**
 * Избранные товары покупателей.
 */
public interface FavoriteRepository extends BaseRepository<Favorite> {
    // Избранные товары покупателя
    List<Favorite> findByCustomerId(Long customerId);

    // Запись в избранном (проверка дубля и удаление)
    Optional<Favorite> findByCustomerIdAndProductId(Long customerId, Long productId);
}
