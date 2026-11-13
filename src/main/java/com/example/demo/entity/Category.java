package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * Категория товаров. Может быть вложенной: у подкатегории указан родитель, у корневой parent = null.
 */
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(nullable = false)
    private String name;

    // Ссылка на родительскую категорию (для подкатегорий)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }
}
