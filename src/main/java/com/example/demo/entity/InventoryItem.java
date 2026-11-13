package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * Позиция склада флориста (материал: розы, упаковка, ленты) с остатком.
 */
@Entity
@Table(name = "inventory_items")
public class InventoryItem extends BaseEntity {

    @Column(nullable = false)
    private String name; // Например: "Роза красная", "Упаковка крафт"

    private Integer quantity; // Остаток
    private String unit;      // "шт", "стебель", "рулон"

    // Причина списания (заполняется при write-off)
    private String writeOffReason;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getWriteOffReason() { return writeOffReason; }
    public void setWriteOffReason(String writeOffReason) { this.writeOffReason = writeOffReason; }
}
