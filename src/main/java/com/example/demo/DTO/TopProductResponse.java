package com.example.demo.DTO;

import java.math.BigDecimal;

/** Товар в рейтинге продаж: сколько штук продано и на какую сумму. */
public record TopProductResponse(Long productId, String name, Long soldQuantity, BigDecimal revenue) {}
