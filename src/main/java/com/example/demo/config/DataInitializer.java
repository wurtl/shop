package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    // Начала интервалов доставки (10:00, 14:00, 18:00); длина каждого — 4 часа
    private static final List<Integer> SLOT_START_HOURS = List.of(10, 14, 18);
    private static final int SLOT_LENGTH_HOURS = 4;

    /**
     * Сам заполнитель: Spring вызывает его один раз после запуска приложения.
     */
    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            InventoryItemRepository inventoryRepository,
            DeliverySlotRepository deliverySlotRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // Если пользователи уже есть, значит база заполнена, пропускаем
            if (userRepository.count() > 0) {
                return;
            }

            log.info("Заполнение базы данных начальными данными...");

            // === ПОЛЬЗОВАТЕЛИ ===
            User florist = user(passwordEncoder, "florist@shop.ru", "florist123", "Флорист Анна", Role.FLORIST);
            florist.setOnShift(true);

            User courier = user(passwordEncoder, "courier@shop.ru", "courier123", "Курьер Петр", Role.COURIER);
            courier.setTransport("Автомобиль, белый Renault");
            courier.setOnShift(true);

            User customer = user(passwordEncoder, "user@mail.ru", "password123", "Иван Покупатель", Role.CUSTOMER);
            customer.setPhone("+79991112233");

            userRepository.saveAll(List.of(
                    user(passwordEncoder, "admin@shop.ru", "admin123", "Главный Администратор", Role.ADMIN),
                    florist, courier, customer));

            // === КАТЕГОРИИ ===
            Category roses = category("Розы");
            Category bouquets = category("Готовые букеты");
            Category gifts = category("Подарки");
            categoryRepository.saveAll(List.of(roses, bouquets, gifts));

            // === ТОВАРЫ ===
            productRepository.saveAll(List.of(
                    product("Букет из 101 розы", "Шикарный букет из красных роз. Идеально для признания в любви.",
                            "7500.00", "Красный", "8 марта", "101 роза, упаковка, лента", 10, true, roses),
                    product("Весенний микс", "Нежный букет из тюльпанов и ромашек.",
                            "3200.00", "Розовый", "День рождения", "15 тюльпанов, 5 веток ромашек", 25, true, bouquets),
                    product("Белые пионы", "Элегантные пионы для особого случая.",
                            "4500.00", "Белый", "Свадьба", "11 пионов", 15, false, bouquets),
                    product("Открытка 'С любовью'", "Красивая открытка ручной работы.",
                            "300.00", "Белый", "Без повода", "Бумага, тиснение", 100, false, gifts)));

            // === СКЛАД ФЛОРИСТА ===
            inventoryRepository.saveAll(List.of(
                    inventoryItem("Роза красная (стебель)", 500, "шт"),
                    inventoryItem("Упаковка крафт (рулон)", 20, "рулон"),
                    inventoryItem("Лента атласная", 50, "метр")));

            // === СЛОТЫ ДОСТАВКИ НА СЕГОДНЯ И ЗАВТРА ===
            LocalDate today = LocalDate.now();
            List<DeliverySlot> slots = new ArrayList<>();
            for (LocalDate date : List.of(today, today.plusDays(1))) {
                for (int hour : SLOT_START_HOURS) {
                    slots.add(slot(date, LocalTime.of(hour, 0), LocalTime.of(hour + SLOT_LENGTH_HOURS, 0)));
                }
            }
            deliverySlotRepository.saveAll(slots);

            log.info("База данных заполнена");
        };
    }

    /**
     * Вспомогательные методы ниже создают сущности, чтобы не повторять сеттеры в initData.
     */
    private static User user(PasswordEncoder encoder, String email, String password, String fullName, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setFullName(fullName);
        user.setRole(role);
        return user;
    }

    private static Category category(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    private static Product product(String name, String description, String price, String color, String occasion,
                                   String composition, int stock, boolean popular, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setColor(color);
        product.setOccasion(occasion);
        product.setComposition(composition);
        product.setStock(stock);
        product.setPopular(popular);
        product.setCategory(category);
        return product;
    }

    private static InventoryItem inventoryItem(String name, int quantity, String unit) {
        InventoryItem item = new InventoryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(unit);
        return item;
    }

    private static DeliverySlot slot(LocalDate date, LocalTime start, LocalTime end) {
        DeliverySlot slot = new DeliverySlot();
        slot.setDate(date);
        slot.setStartTime(start);
        slot.setEndTime(end);
        return slot;
    }
}
