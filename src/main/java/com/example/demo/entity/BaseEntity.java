package com.example.demo.entity;

import jakarta.persistence.*;

/** Общий предок сущностей: единый первичный ключ. */
@MappedSuperclass
public abstract class BaseEntity {

    // Первичный ключ; значение выдаёт база (автоинкремент)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
