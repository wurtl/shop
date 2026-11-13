package com.example.demo.controller;

import com.example.demo.DTO.QuantityRequest;
import com.example.demo.DTO.WriteOffRequest;
import com.example.demo.entity.InventoryItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.service.InventoryService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import com.example.demo.web.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/florist")
public class FloristController extends StaffController {

    private final OrderService orderService;
    private final InventoryService inventoryService;

    // userService передаём наследнику (StaffController), он нужен для смен
    public FloristController(UserService userService, OrderService orderService, InventoryService inventoryService) {
        super(userService);
        this.orderService = orderService;
        this.inventoryService = inventoryService;
    }

    // --- ОЧЕРЕДЬ СБОРКИ ---

    /**
     * Очередь сборки: оплаченные заказы, которые ещё никто не взял в работу
     * (сначала те, у которых ближе время доставки).
     * Тот же список отдаётся по адресу /notifications — как уведомления о новых заказах.
     */
    @GetMapping({"/orders/queue", "/notifications"})
    public List<Order> getQueue() {
        return orderService.getOrdersForAssembly();
    }

    /**
     * Заказы, закреплённые за текущим флористом (кроме отменённых).
     */
    @GetMapping("/orders/my")
    public List<Order> getMyOrders(@CurrentUser User user) {
        return orderService.getFloristOrders(user);
    }

    /**
     * Подробности заказа по ID.
     */
    @GetMapping("/orders/{id}")
    public Order getOrderDetails(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    /**
     * Принять заказ в работу: статус «Оплачен» → «Принят», заказ закрепляется за этим флористом.
     */
    @PostMapping("/orders/{id}/start")
    public Order startOrder(@CurrentUser User user, @PathVariable Long id) {
        return orderService.accept(user, id);
    }

    /**
     * Завершить сборку: статус «Принят» → «Собран». После этого заказ появляется в очереди курьера.
     * Завершить может только флорист, принявший этот заказ.
     */
    @PostMapping("/orders/{id}/complete")
    public Order completeOrder(@CurrentUser User user, @PathVariable Long id) {
        return orderService.completeAssembly(user, id);
    }

    // --- СКЛАД ---

    /**
     * Все позиции склада (цветы, упаковка, ленты) с остатками.
     */
    @GetMapping("/inventory")
    public List<InventoryItem> getInventory() {
        return inventoryService.getAllItems();
    }

    /**
     * Установить новый остаток позиции склада (например, после пересчёта).
     * Новое количество передаётся в QuantityRequest.
     */
    @PatchMapping("/inventory/{itemId}")
    public InventoryItem updateStock(@PathVariable Long itemId, @RequestBody QuantityRequest request) {
        return inventoryService.updateStock(itemId, request.quantity());
    }

    /**
     * Списать часть позиции (увяло, повреждено и т.п.).
     * В теле (WriteOffRequest): позиция, количество и причина. Нельзя списать больше, чем есть.
     * Каждое списание сохраняется в истории — по ней считается статистика администратора.
     */
    @PostMapping("/inventory/write-off")
    public InventoryItem writeOff(@RequestBody WriteOffRequest request) {
        return inventoryService.writeOff(request.itemId(), request.quantity(), request.reason());
    }
}
