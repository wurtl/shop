package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Товар каталога: букет, композиция или подарок.
 */
@Entity
@Table(name = "products")
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private String color;      // Для фильтра цветов
    private String occasion;   // Для фильтра поводов (8 марта, свадьба)
    private String composition;// Состав (из карточки товара)
    private String imageUrl;   // Ссылка на фото

    private Integer stock = 0; // Наличие на складе
    private Boolean popular = false; // Хит продаж
    private Boolean active = true;   // На продаже или снят

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getOccasion() { return occasion; }
    public void setOccasion(String occasion) { this.occasion = occasion; }

    public String getComposition() { return composition; }
    public void setComposition(String composition) { this.composition = composition; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Boolean getPopular() { return popular; }
    public void setPopular(Boolean popular) { this.popular = popular; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
