package com.example.demo.DTO;

/** Всего списано за всё время и текущие остатки склада (в единицах). */
public record InventoryStatsResponse(long writeOffs, long leftovers) {}
