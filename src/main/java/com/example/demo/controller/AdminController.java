package com.example.demo.controller;

import com.example.demo.DTO.*;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.service.AdminService;
import com.example.demo.service.CatalogService;
import com.example.demo.service.StatsService;
import com.example.demo.service.UserService;
import com.example.demo.web.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для администратора.
 * Предоставляет полный доступ к управлению пользователями, товарами, категориями и аналитикой.
 * Наследуется от ProfileController, чтобы переиспользовать общие методы (например, просмотр профиля).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController extends ProfileController {

    private final UserService userService;
    private final AdminService adminService;
    private final CatalogService catalogService;
    private final StatsService statsService;

    public AdminController(UserService userService, AdminService adminService, CatalogService catalogService,
                           StatsService statsService) {
        this.userService = userService;
        this.adminService = adminService;
        this.catalogService = catalogService;
        this.statsService = statsService;
    }


    // --- ПРОФИЛЬ ---
    /**
     * Смена пароля администратора.
     * Принимает старый и новый пароль.
     * Сервис userService проверяет, совпадает ли старый пароль с текущим в базе, и шифрует новый.
     */
    @PatchMapping("/profile/password")
    public MessageResponse changePassword(@CurrentUser User user, @RequestBody PasswordChangeRequest request) {
        userService.changePassword(user, request);
        return new MessageResponse("Пароль изменен");
    }


    // --- ПОЛЬЗОВАТЕЛИ ---

    /**
     * Получить список всех пользователей системы (админы, флористы, курьеры, покупатели).
     * Используется .stream().map(UserResponse::from), чтобы скрыть хеши паролей и вернуть только безопасные данные.
     */
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers().stream().map(UserResponse::from).toList();
    }

    /**
     * Получить карточку конкретного пользователя по его ID.
     */
    @GetMapping("/users/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return UserResponse.from(userService.getUserById(id));
    }

    /**
     * Создать нового сотрудника (флориста, курьера или другого админа).
     * Принимает CreateUserRequest (email, пароль, имя, роль).
     * Сервис зашифрует пароль перед сохранением в базу.
     */
    @PostMapping("/users")
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return UserResponse.from(userService.createUser(request));
    }

    /**
     * Изменить роль пользователя (например, сделать покупателя флористом).
     * В RoleRequest передается только новая роль.
     */
    @PatchMapping("/users/{id}/role")
    public UserResponse changeRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        return UserResponse.from(userService.changeRole(id, request.role()));
    }

    /**
     * Удалить пользователя из системы по его ID.
     * Возвращает статус 204 No Content (успешно удалено, тело ответа отсутствует).
     */
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }


    // --- ТОВАРЫ ---


    /**
     * Получить список всех товаров (включая те, что сняты с продажи).
     */
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return catalogService.getAllProducts();
    }

    /**
     * Создать новый товар.
     * В отличие от других методов, здесь принимается целая сущность Product.
     */
    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        return adminService.createProduct(product);
    }

    /**
     * Обновить данные товара (название, цену, остаток). Остальные поля через этот метод не меняются.
     * Принимает ProductPatchRequest (DTO), чтобы админ не мог случайно изменить ID или другие системные поля.
     */
    @PatchMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductPatchRequest patch) {
        return adminService.updateProduct(id, patch);
    }

    /**
     * Удалить товар из базы данных.
     * Возвращает 204 No Content.
     */
    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
    }

    /**
     * Точечное обновление остатка на складе.
     * Создает временный ProductPatchRequest, передавая в него только новое количество (остальные поля null),
     * и передает его в общий метод updateProduct.
     */
    @PatchMapping("/products/{id}/stock")
    public Product updateStock(@PathVariable Long id, @RequestBody StockRequest request) {
        return adminService.updateProduct(id, new ProductPatchRequest(null, null, request.stock()));
    }


    // --- КАТЕГОРИИ ---


    /**
     * Получить список всех категорий.
     */
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return catalogService.getAllCategories();
    }

    /**
     * Создать новую категорию (например, "Тюльпаны").
     */
    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) {
        return adminService.createCategory(category);
    }

    /**
     * Обновить категорию (переименовать или назначить родительскую категорию).
     */
    @PatchMapping("/categories/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category patch) {
        return adminService.updateCategory(id, patch);
    }

    /**
     * Удалить категорию.
     * Возвращает 204 No Content.
     */
    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
    }


    // --- АНАЛИТИКА ---

    /**
     * Получить статистику продаж (выручка, количество заказов, средний чек).
     * Данные формируются в StatsService на основе таблицы заказов.
     */
    @GetMapping("/stats/sales")
    public SalesStatsResponse getSalesStats() {
        return statsService.sales();
    }

    /**
     * Получить топ самых продаваемых букетов.
     * StatsService делает запрос в БД и сортирует товары по количеству продаж.
     */
    @GetMapping("/stats/top")
    public List<TopProductResponse> getTopProducts() {
        return statsService.topProducts();
    }

    /**
     * Получить статистику по складу (сколько списано, сколько осталось).
     */
    @GetMapping("/stats/inventory")
    public InventoryStatsResponse getInventoryStats() {
        return statsService.inventory();
    }
}