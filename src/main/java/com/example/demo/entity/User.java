package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Пользователь системы. Роль (Role) определяет, какие разделы API ему доступны.
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    // Хэш пароля никогда не попадает в JSON (в том числе когда пользователь вложен в заказ)
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String fullName;
    private String phone;

    // CUSTOMER, FLORIST, COURIER или ADMIN; в базе хранится строкой
    @Enumerated(EnumType.STRING)
    private Role role;

    // Для курьера (из эндпоинта GET /api/courier/profile)
    private String transport;

    // Для флориста и курьера (смены)
    private Boolean onShift = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public Boolean getOnShift() { return onShift; }
    public void setOnShift(Boolean onShift) { this.onShift = onShift; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
