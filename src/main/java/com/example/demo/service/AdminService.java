package com.example.demo.service;

import com.example.demo.DTO.ProductPatchRequest;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.Require;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;

/** Изменение каталога администратором . */
@Service
public class AdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Репозитории товаров и категорий внедряются через конструктор
    public AdminService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // --- Товары ---
    /**
     * Создать товар.
     */
    public Product createProduct(Product product) { return productRepository.save(product); }

    /**
     * Частичное обновление товара: меняются только те поля, которые переданы (не null).
     */
    public Product updateProduct(Long id, ProductPatchRequest patch) {
        Product p = productRepository.getOrThrow(id);
        if (patch.price() != null) Require.positive(patch.price(), "Цена должна быть больше нуля");
        if (patch.stock() != null) Require.nonNegative(patch.stock(), "Остаток не может быть отрицательным");
        if (patch.name() != null) p.setName(patch.name());
        if (patch.price() != null) p.setPrice(patch.price());
        if (patch.stock() != null) p.setStock(patch.stock());
        return productRepository.save(p);
    }

    /**
     * Удалить товар.
     */
    public void deleteProduct(Long id) { productRepository.deleteById(id); }

    // --- Категории ---
    /**
     * Создать категорию.
     */
    public Category createCategory(Category category) { return categoryRepository.save(category); }

    /**
     * Частичное обновление категории: название и/или родитель, если переданы.
     */
    public Category updateCategory(Long id, Category patch) {
        Category c = categoryRepository.getOrThrow(id);
        if (patch.getName() != null) c.setName(patch.getName());
        if (patch.getParent() != null) c.setParent(patch.getParent());
        return categoryRepository.save(c);
    }

    /**
     * Удалить категорию.
     */
    public void deleteCategory(Long id) { categoryRepository.deleteById(id); }
}
