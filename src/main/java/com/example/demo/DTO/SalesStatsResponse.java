package com.example.demo.DTO;

import java.math.BigDecimal;

/** Выручка, число оплаченных заказов и средний чек. */
public record SalesStatsResponse(BigDecimal revenue, long orders, BigDecimal avgCheck) {}
