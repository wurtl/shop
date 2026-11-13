package com.example.demo.repository;

import com.example.demo.entity.Payment;

import java.util.Optional;

/**
 * Платежи по заказам.
 */
public interface PaymentRepository extends BaseRepository<Payment> {
    // Платёж по ID заказа
    Optional<Payment> findByOrderId(Long orderId);

    @Override
    default String notFoundMessage() { return "Платёж не найден"; }
}
