package com.example.demo.controller;

import com.example.demo.DTO.CreateOrderRequest;
import com.example.demo.DTO.MessageResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.service.OrderService;
import com.example.demo.web.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Заказы покупателя: оформление, история, просмотр и отмена.
 * Покупатель видит только свои заказы — владельца проверяет OrderService.
 */
@RestController
@RequestMapping("/api/customer/orders")
public class OrderController {

    private final OrderService orderService;

    // Внедряем сервис заказов через конструктор
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Оформить заказ из текущей корзины.
     * В теле (CreateOrderRequest): адрес, текст открытки и время доставки.
     * Заказ создаётся в статусе «Ожидает оплаты», корзина после этого очищается.
     */
    @PostMapping
    public Order createOrder(@CurrentUser User user, @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(user, request);
    }

    /**
     * История заказов текущего покупателя (сначала новые).
     */
    @GetMapping
    public List<Order> getMyOrders(@CurrentUser User user) {
        return orderService.getCustomerOrders(user);
    }

    /**
     * Получить один заказ по ID.
     * Для чужого заказа вернётся 404, как будто его не существует.
     */
    @GetMapping("/{id}")
    public Order getOrder(@CurrentUser User user, @PathVariable Long id) {
        return orderService.getCustomerOrder(user, id);
    }

    /**
     * Отменить заказ.
     * Возможно, пока флорист не принял заказ в работу (статусы «Ожидает оплаты» и «Оплачен»);
     * иначе вернётся ошибка 400 о недопустимом переходе статуса.
     */
    @PostMapping("/{id}/cancel")
    public MessageResponse cancelOrder(@CurrentUser User user, @PathVariable Long id) {
        orderService.cancelOrder(user, id);
        return new MessageResponse("Заказ отменен");
    }
}
