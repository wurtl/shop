package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.entity.DeliverySlot;
import com.example.demo.entity.Product;
import com.example.demo.service.CatalogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CatalogService catalogService;

    public CustomerController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    /**
     * Получить список корневых категорий (для главного меню каталога).
     * Возвращает только категории верхнего уровня, без вложенных подкатегорий.
     */
    @GetMapping("/categories")
    public List<Category> getCategories() {
        return catalogService.getRootCategories();
    }

    /**
     * Получить детали конкретной категории по её ID (включая подкатегории).
     */
    @GetMapping("/categories/{id}")
    public Category getCategory(@PathVariable Long id) {
        return catalogService.getCategoryById(id);
    }

    /**
     * Получить список всех товаров (букетов), доступных для продажи.
     */
    @GetMapping("/products")
    public List<Product> getProducts() {
        return catalogService.getAllProducts();
    }

    /**
     * Получить карточку конкретного товара по ID (с описанием, составом, фото).
     */
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable Long id) {
        return catalogService.getProductById(id);
    }

    /**
     * Получить блок «Хиты продаж» (товары, которые пользуются наибольшим спросом).
     */
    @GetMapping("/products/popular")
    public List<Product> getPopularProducts() {
        return catalogService.getPopularProducts();
    }

    /**
     * Поиск товаров по названию или описанию.
     * В URL передается параметр q (например, /products/search?q=роза).
     */
    @GetMapping("/products/search")
    public List<Product> searchProducts(@RequestParam String q) {
        return catalogService.searchProducts(q);
    }

    /**
     * Справочник доступных поводов (для фильтрации товаров).
     */
    @GetMapping("/occasions")
    public List<String> getOccasions() {
        return List.of("8 марта", "День рождения", "Свадьба", "Без повода");
    }

    /**
     * Справочник доступных цветов (для фильтрации товаров).
     */
    @GetMapping("/colors")
    public List<String> getColors() {
        return List.of("Красный", "Белый", "Розовый", "Желтый");
    }


    // --- ДОСТАВКА ---


    /**
     * Получить доступные интервалы времени для доставки на конкретную дату.
     *  Дата в формате YYYY-MM-DD. Аннотация @DateTimeFormat автоматически
     * конвертирует строку из URL в объект LocalDate.
     */
    @GetMapping("/delivery/slots")
    public List<DeliverySlot> getSlots(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return catalogService.getAvailableSlots(date);
    }
}