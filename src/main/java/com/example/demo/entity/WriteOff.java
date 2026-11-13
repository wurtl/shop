package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Запись о списании со склада: история, из которой считается статистика. */
@Entity
@Table(name = "write_offs")
public class WriteOff extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    private Integer quantity;
    private String reason;
    private LocalDateTime createdAt = LocalDateTime.now();

    public static WriteOff of(InventoryItem item, Integer quantity, String reason) {
        WriteOff w = new WriteOff();
        w.item = item;
        w.quantity = quantity;
        w.reason = reason;
        return w;
    }

    public InventoryItem getItem() { return item; }
    public Integer getQuantity() { return quantity; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
