package com.example.demo.repository;

import com.example.demo.entity.InventoryItem;
import org.springframework.data.jpa.repository.Query;

/**
 * Склад флориста: цветы, упаковка, ленты и т.п.
 */
public interface InventoryItemRepository extends BaseRepository<InventoryItem> {

    /** Сколько единиц всех позиций сейчас лежит на складе. */
    @Query("select coalesce(sum(i.quantity), 0) from InventoryItem i")
    long totalQuantity();

    @Override
    default String notFoundMessage() { return "Позиция склада не найдена"; }
}
