package com.example.demo.DTO;

/**
 * Запрос «списать со склада»: ID позиции склада, количество и причина.
 */
public record WriteOffRequest(Long itemId, Integer quantity, String reason) {}
