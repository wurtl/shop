package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Позиция корзины: товар и его количество. Покупатель и товар — в CustomerProductLink.
 */
@Entity
@Table(name = "cart_items")
public class CartItem extends CustomerProductLink {

    // Сколько штук товара лежит в корзине
    private Integer quantity = 1;

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    /** Стоимость позиции: цена товара × количество. */
    public BigDecimal subtotal() {
        return getProduct().getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
