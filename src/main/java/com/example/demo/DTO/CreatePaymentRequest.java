package com.example.demo.DTO;

/**
 * Запрос «создать платёж»: ID заказа. Сумму сервер берёт из самого заказа.
 */
public record CreatePaymentRequest(Long orderId) {}
