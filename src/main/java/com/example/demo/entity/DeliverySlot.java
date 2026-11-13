package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Интервал доставки: дата, время начала и конца, признак «доступен для брони».
 */
@Entity
@Table(name = "delivery_slots")
public class DeliverySlot extends BaseEntity {

    @Column(nullable = false)
    private LocalDate date; // Дата доставки

    private LocalTime startTime; // Начало интервала
    private LocalTime endTime;   // Конец интервала

    private Boolean available = true; // Доступен ли слот для брони

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}
