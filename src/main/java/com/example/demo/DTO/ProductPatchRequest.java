package com.example.demo.DTO;

import java.math.BigDecimal;

/** Частичное обновление товара: null-поля не меняются. */
public record ProductPatchRequest(String name, BigDecimal price, Integer stock) {}
