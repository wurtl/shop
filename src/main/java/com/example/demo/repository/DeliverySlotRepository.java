package com.example.demo.repository;

import com.example.demo.entity.DeliverySlot;

import java.time.LocalDate;
import java.util.List;

/**
 * Интервалы доставки (например, 10:00–14:00 на выбранную дату).
 */
public interface DeliverySlotRepository extends BaseRepository<DeliverySlot> {
    // Доступные слоты на конкретную дату
    List<DeliverySlot> findByDateAndAvailableTrue(LocalDate date);
}
