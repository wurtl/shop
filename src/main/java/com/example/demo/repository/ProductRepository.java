package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Товары каталога.
 */
public interface ProductRepository extends BaseRepository<Product> {
    // Блок «Хиты продаж»
    List<Product> findByPopularTrue();

    // Поиск по названию или описанию
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    // Фильтрация каталога (упрощённая)
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Списать остаток одним SQL-запросом: условие «хватает товара» проверяется прямо в базе,
     * поэтому два покупателя одновременно не смогут забрать последние штуки (остаток не уйдёт в минус).
     * Возвращает число изменённых строк: 1 — списано, 0 — товара не хватает.
     */
    @Modifying
    @Query("update Product p set p.stock = p.stock - :quantity where p.id = :id and p.stock >= :quantity")
    int decreaseStock(Long id, int quantity);

    /**
     * Вернуть товар на остаток (при отмене заказа).
     */
    @Modifying
    @Query("update Product p set p.stock = p.stock + :quantity where p.id = :id")
    int increaseStock(Long id, int quantity);

    @Override
    default String notFoundMessage() { return "Товар не найден"; }
}
