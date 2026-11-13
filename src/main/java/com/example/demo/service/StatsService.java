package com.example.demo.service;

import com.example.demo.DTO.InventoryStatsResponse;
import com.example.demo.DTO.SalesStatsResponse;
import com.example.demo.DTO.TopProductResponse;
import com.example.demo.entity.OrderStatus;
import com.example.demo.repository.InventoryItemRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.WriteOffRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/** Аналитика для админки. В продажи попадают только оплаченные заказы (не «Ожидает оплаты» и не «Отменён»). */
@Service
@Transactional(readOnly = true)
public class StatsService {

    // Сколько товаров показывать в рейтинге продаж
    private static final int TOP_PRODUCTS_LIMIT = 10;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryItemRepository inventoryRepository;
    private final WriteOffRepository writeOffRepository;

    // Репозитории внедряются через конструктор
    public StatsService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        InventoryItemRepository inventoryRepository, WriteOffRepository writeOffRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.writeOffRepository = writeOffRepository;
    }

    /**
     * Продажи: выручка, число оплаченных заказов и средний чек (выручка / число заказов).
     * Если заказов ещё нет, средний чек равен 0.
     */
    public SalesStatsResponse sales() {
        var paid = OrderStatus.paidStatuses();
        long orders = orderRepository.countByStatusIn(paid);
        BigDecimal revenue = Optional.ofNullable(orderRepository.sumTotalPrice(paid)).orElse(BigDecimal.ZERO);
        BigDecimal avgCheck = orders == 0
                ? BigDecimal.ZERO
                : revenue.divide(BigDecimal.valueOf(orders), 2, RoundingMode.HALF_UP);
        return new SalesStatsResponse(revenue, orders, avgCheck);
    }

    /**
     * Топ-10 товаров по количеству проданных штук среди оплаченных заказов.
     */
    public List<TopProductResponse> topProducts() {
        return orderItemRepository.findTopProducts(OrderStatus.paidStatuses(), PageRequest.of(0, TOP_PRODUCTS_LIMIT));
    }

    /**
     * Склад: сколько единиц списано за всё время и сколько лежит сейчас.
     */
    public InventoryStatsResponse inventory() {
        return new InventoryStatsResponse(writeOffRepository.totalQuantity(), inventoryRepository.totalQuantity());
    }
}
