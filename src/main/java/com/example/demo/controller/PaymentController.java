package com.example.demo.controller;

import com.example.demo.DTO.CreatePaymentRequest;
import com.example.demo.DTO.PaymentResponse;
import com.example.demo.entity.User;
import com.example.demo.service.PaymentService;
import com.example.demo.web.CurrentUser;
import org.springframework.web.bind.annotation.*;

/**
 * Оплата заказов. Имитация платежа (см. {@link PaymentService}):
 * Порядок: create → (клиент «платит» по ссылке) → confirm → заказ становится «Оплачен».
 */
@RestController
@RequestMapping("/api/customer/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // Внедряем сервис платежей через конструктор
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Создать платёж по заказу, ожидающему оплаты.
     * В теле (CreatePaymentRequest) передаётся только orderId, сумма берётся из заказа.
     * Повторный вызов вернёт уже созданный платёж.
     */
    @PostMapping("/create")
    public PaymentResponse createPayment(@CurrentUser User user, @RequestBody CreatePaymentRequest request) {
        return PaymentResponse.from(paymentService.createPayment(user, request.orderId()));
    }

    /**
     * Имитация «банк подтвердил оплату»: платёж получает статус SUCCESS,
     * а заказ переходит из «Ожидает оплаты» в «Оплачен».
     */
    @PostMapping("/{id}/confirm")
    public PaymentResponse confirmPayment(@CurrentUser User user, @PathVariable Long id) {
        return PaymentResponse.from(paymentService.confirm(user, id));
    }

    /**
     * Узнать текущий статус платежа (PENDING, SUCCESS или FAILED).
     * Чужой платёж увидеть нельзя — сервис проверяет владельца заказа.
     */
    @GetMapping("/{id}/status")
    public PaymentResponse getPaymentStatus(@CurrentUser User user, @PathVariable Long id) {
        return PaymentResponse.from(paymentService.getPayment(user, id));
    }
}
