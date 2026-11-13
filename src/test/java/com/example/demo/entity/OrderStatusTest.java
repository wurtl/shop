package com.example.demo.entity;

import com.example.demo.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Проверка жизненного цикла заказа. Тесты не требуют базы данных: работают с объектом Order напрямую.
 */
class OrderStatusTest {

    /**
     * Новый заказ создаётся в статусе «Ожидает оплаты».
     */
    @Test
    void newOrderAwaitsPayment() {
        assertEquals(OrderStatus.AWAITING_PAYMENT, new Order().getStatus());
    }

    /**
     * Заказ проходит весь путь: Оплачен → Принят → Собран → Доставляется → Доставлен.
     */
    @Test
    void orderGoesThroughFullLifecycle() {
        Order order = new Order();
        for (OrderStatus next : new OrderStatus[]{
                OrderStatus.PAID, OrderStatus.ACCEPTED, OrderStatus.ASSEMBLED,
                OrderStatus.DELIVERING, OrderStatus.DELIVERED}) {
            order.moveTo(next);
            assertEquals(next, order.getStatus());
        }
    }

    /**
     * Пропускать статусы нельзя, при отказе статус остаётся прежним.
     */
    @Test
    void cannotSkipStatuses() {
        Order order = new Order();
        assertThrows(BusinessException.class, () -> order.moveTo(OrderStatus.DELIVERING));
        assertEquals(OrderStatus.AWAITING_PAYMENT, order.getStatus());
    }

    /**
     * Отменить можно до приёма заказа флористом, после — нельзя.
     */
    @Test
    void canCancelOnlyBeforeAcceptance() {
        Order paid = new Order();
        paid.moveTo(OrderStatus.PAID);
        paid.moveTo(OrderStatus.CANCELLED);

        Order accepted = new Order();
        accepted.moveTo(OrderStatus.PAID);
        accepted.moveTo(OrderStatus.ACCEPTED);
        assertThrows(BusinessException.class, () -> accepted.moveTo(OrderStatus.CANCELLED));
    }

    /**
     * Из конечных статусов («Доставлен», «Отменён») переходов нет.
     */
    @Test
    void finalStatusesHaveNoTransitions() {
        assertTrue(OrderStatus.DELIVERED.allowedNext().isEmpty());
        assertTrue(OrderStatus.CANCELLED.allowedNext().isEmpty());
    }

    /**
     * Названия статусов совпадают с теми, что показываются пользователю.
     */
    @Test
    void titlesAreInRussian() {
        assertEquals("Ожидает оплаты", OrderStatus.AWAITING_PAYMENT.getTitle());
        assertEquals("Оплачен", OrderStatus.PAID.getTitle());
        assertEquals("Принят", OrderStatus.ACCEPTED.getTitle());
        assertEquals("Доставляется", OrderStatus.DELIVERING.getTitle());
        assertEquals("Доставлен", OrderStatus.DELIVERED.getTitle());
    }
}
