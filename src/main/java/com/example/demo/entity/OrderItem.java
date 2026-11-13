package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Позиция заказа: товар, количество и цена на момент покупки.
 */
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer quantity;

    // Цена фиксируется на момент покупки
    private BigDecimal price;

    /**
     * Создать позицию заказа из позиции корзины, зафиксировав текущую цену товара.
     */
    public static OrderItem from(Order order, CartItem cartItem) {
        OrderItem item = new OrderItem();
        item.order = order;
        item.product = cartItem.getProduct();
        item.quantity = cartItem.getQuantity();
        item.price = cartItem.getProduct().getPrice();
        return item;
    }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
