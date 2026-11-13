package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.entity.DeliverySlot;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.DeliverySlotRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/** Чтение каталога: общее для витрины покупателя и админки. */
@Service
public class CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final DeliverySlotRepository deliverySlotRepository;

    // Репозитории каталога внедряются через конструктор
    public CatalogService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          DeliverySlotRepository deliverySlotRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.deliverySlotRepository = deliverySlotRepository;
    }

    /**
     * Категории верхнего уровня (для главного меню).
     */
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    /**
     * Все категории вместе с подкатегориями (для админки).
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Категория по ID; если не найдена — 404.
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.getOrThrow(id);
    }

    /**
     * Все товары.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Товар по ID; если не найден — 404.
     */
    public Product getProductById(Long id) {
        return productRepository.getOrThrow(id);
    }

    /**
     * Товары с отметкой «Хит продаж».
     */
    public List<Product> getPopularProducts() {
        return productRepository.findByPopularTrue();
    }

    /**
     * Поиск по названию или описанию без учёта регистра.
     */
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    /**
     * Свободные интервалы доставки на выбранную дату.
     */
    public List<DeliverySlot> getAvailableSlots(LocalDate date) {
        return deliverySlotRepository.findByDateAndAvailableTrue(date);
    }
}
