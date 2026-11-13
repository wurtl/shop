package com.example.demo.DTO;

import java.time.LocalDateTime;

/**
 * Запрос «оформить заказ»: адрес доставки, текст открытки и желаемое время доставки.
 */
public record CreateOrderRequest(String address, String cardText, LocalDateTime deliverySlot) {}
