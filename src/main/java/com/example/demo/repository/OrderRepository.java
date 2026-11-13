package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;

import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Заказы. Фильтры по статусам обслуживают очереди флориста и курьера, а также статистику.
 */
public interface OrderRepository extends BaseRepository<Order> {
    // История заказов покупателя (сначала новые)
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // Заказы в нужном статусе, сначала те, у которых ближе время доставки
    List<Order> findByStatusOrderByDeliverySlotAsc(OrderStatus status);

    // Заказы, закреплённые за флористом / курьером
    List<Order> findByFloristIdAndStatusNot(Long floristId, OrderStatus status);
    List<Order> findByCourierId(Long courierId);

    // Статистика продаж по заказам в переданных статусах
    long countByStatusIn(Collection<OrderStatus> statuses);

    @Query("select sum(o.totalPrice) from Order o where o.status in :statuses")
    // null, если подходящих заказов нет (обрабатывается в StatsService)
    BigDecimal sumTotalPrice(Collection<OrderStatus> statuses);

    @Override
    default String notFoundMessage() { return "Заказ не найден"; }
}
