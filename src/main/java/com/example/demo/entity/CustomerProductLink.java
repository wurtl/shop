package com.example.demo.entity;

import jakarta.persistence.*;

/** Связь «покупатель — товар»: общая основа для корзины и избранного. */
@MappedSuperclass
public abstract class CustomerProductLink extends BaseEntity implements CustomerOwned {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Override
    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
