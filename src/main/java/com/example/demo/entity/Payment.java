package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Платёж по заказу (имитация). Один заказ — один платёж.
 */
@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    // Заказ, который оплачивается
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Сумма платежа; копируется из заказа в момент создания
    private BigDecimal amount;

    // PENDING (ждём оплату) → SUCCESS или FAILED
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String paymentUrl; // Ссылка на оплату (для редиректа клиента)

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
}
