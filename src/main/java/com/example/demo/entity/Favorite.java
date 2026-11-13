package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * Запись «товар в избранном у покупателя». Все поля уже есть в CustomerProductLink.
 */
@Entity
@Table(name = "favorites")
public class Favorite extends CustomerProductLink {
}
