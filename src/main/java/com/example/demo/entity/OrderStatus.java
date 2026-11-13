package com.example.demo.entity;

import java.util.EnumSet;
import java.util.Set;

/**
 *
 * AWAITING_PAYMENT → PAID → ACCEPTED → ASSEMBLED → DELIVERING → DELIVERED
 *
 * Отменить заказ можно, пока флорист не принял его в работу (до ACCEPTED).
 */
public enum OrderStatus {
    AWAITING_PAYMENT("Ожидает оплаты"),
    PAID("Оплачен"),
    ACCEPTED("Принят"),
    ASSEMBLED("Собран"),
    DELIVERING("Доставляется"),
    DELIVERED("Доставлен"),
    CANCELLED("Отменён");

    // Название статуса для показа пользователю
    private final String title;

    OrderStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Статусы, в которые можно перейти из текущего.
     */
    public Set<OrderStatus> allowedNext() {
        return switch (this) {
            case AWAITING_PAYMENT -> EnumSet.of(PAID, CANCELLED);
            case PAID -> EnumSet.of(ACCEPTED, CANCELLED);
            case ACCEPTED -> EnumSet.of(ASSEMBLED);
            case ASSEMBLED -> EnumSet.of(DELIVERING);
            case DELIVERING -> EnumSet.of(DELIVERED);
            case DELIVERED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
        };
    }

    /** Статусы заказов, по которым деньги получены (идут в статистику продаж). */
    public static Set<OrderStatus> paidStatuses() {
        return EnumSet.of(PAID, ACCEPTED, ASSEMBLED, DELIVERING, DELIVERED);
    }

    /**
     * Можно ли перейти из текущего статуса в указанный.
     */
    public boolean canMoveTo(OrderStatus next) {
        return allowedNext().contains(next);
    }
}
