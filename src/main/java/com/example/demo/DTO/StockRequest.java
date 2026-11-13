package com.example.demo.DTO;

/**
 * Запрос «изменить остаток товара в каталоге»: новое количество.
 */
public record StockRequest(Integer stock) {}
