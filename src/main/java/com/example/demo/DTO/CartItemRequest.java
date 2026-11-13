package com.example.demo.DTO;


public record CartItemRequest(Long productId, Integer quantity) {

    /** Если количество не указано — добавляем одну штуку. */
    public int quantityOrDefault() {
        return quantity != null ? quantity : 1;
    }
}
