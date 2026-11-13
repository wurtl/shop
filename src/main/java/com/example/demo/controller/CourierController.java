package com.example.demo.controller;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import com.example.demo.web.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/courier")
public class CourierController extends StaffController {

    private final OrderService orderService;

    // userService передаём наследнику (StaffController), он нужен для смен
    public CourierController(UserService userService, OrderService orderService) {
        super(userService);
        this.orderService = orderService;
    }

    /**
     * Очередь доставки: собранные заказы, которые ждут курьера
     * (сначала те, у которых ближе время доставки).
     */
    @GetMapping("/orders/queue")
    public List<Order> getQueue() {
        return orderService.getOrdersForDelivery();
    }

    /**
     * Заказы, которые взял текущий курьер.
     */
    @GetMapping("/orders/my")
    public List<Order> getMyOrders(@CurrentUser User user) {
        return orderService.getCourierOrders(user);
    }

    /**
     * Забрать заказ и поехать: статус «Собран» → «Доставляется», заказ закрепляется за курьером.
     */
    @PostMapping("/orders/{id}/start")
    public Order startDelivery(@CurrentUser User user, @PathVariable Long id) {
        return orderService.startDelivery(user, id);
    }

    /**
     * Отметить заказ врученным: статус «Доставляется» → «Доставлен».
     * Завершить может только курьер, взявший этот заказ.
     */
    @PostMapping("/orders/{id}/complete")
    public Order completeDelivery(@CurrentUser User user, @PathVariable Long id) {
        return orderService.completeDelivery(user, id);
    }
}
