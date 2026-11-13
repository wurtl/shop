package com.example.demo.DTO;

import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentStatus;

/**
 * Ответ о платеже: ID платежа и заказа, ссылка на оплату и статус.
 */
public record PaymentResponse(Long paymentId, Long orderId, String paymentUrl, PaymentStatus status) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(payment.getId(), payment.getOrder().getId(),
                payment.getPaymentUrl(), payment.getStatus());
    }
}
