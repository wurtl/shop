package com.example.demo.entity;

import com.example.demo.exception.BusinessException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Заказ покупателя. Статус меняется только через moveTo(), который проверяет допустимость перехода
 * (правила — в OrderStatus).
 */
@Entity
@Table(name = "orders")
public class Order extends BaseEntity implements CustomerOwned {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Флорист, который принял заказ в работу (может быть null)
    @ManyToOne
    @JoinColumn(name = "florist_id")
    private User florist;

    // Курьер, который взял заказ на доставку (может быть null)
    @ManyToOne
    @JoinColumn(name = "courier_id")
    private User courier;

    private String address;
    private LocalDateTime deliverySlot; // Дата и время доставки
    private String cardText;            // Текст открытки
    private BigDecimal totalPrice;

    // Текущий статус; в базе хранится строкой (AWAITING_PAYMENT, PAID, ...)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.AWAITING_PAYMENT;

    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Единственный способ сменить статус заказа.
     * Если переход недопустим (например, «Ожидает оплаты» → «Доставляется»), бросает BusinessException.
     */
    public void moveTo(OrderStatus next) {
        if (!status.canMoveTo(next)) {
            throw new BusinessException("Нельзя перевести заказ из статуса «%s» в «%s»"
                    .formatted(status.getTitle(), next.getTitle()));
        }
        status = next;
    }

    /**
     * Название статуса для клиента (попадает в JSON рядом с кодом статуса).
     */
    public String getStatusTitle() { return status.getTitle(); }

    @Override
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public User getFlorist() { return florist; }
    public void setFlorist(User florist) { this.florist = florist; }

    public User getCourier() { return courier; }
    public void setCourier(User courier) { this.courier = courier; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getDeliverySlot() { return deliverySlot; }
    public void setDeliverySlot(LocalDateTime deliverySlot) { this.deliverySlot = deliverySlot; }

    public String getCardText() { return cardText; }
    public void setCardText(String cardText) { this.cardText = cardText; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public OrderStatus getStatus() { return status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
