package com.example.demo.DTO;

import com.example.demo.entity.Role;

/**
 * Запрос «зарегистрироваться». Поля роли здесь нет: саморегистрация создаёт только покупателя.
 */
public record RegisterRequest(
        String email,
        String password,
        String fullName,
        String phone
) {
    /** Саморегистрация всегда создаёт покупателя. */
    public CreateUserRequest asCustomer() {
        return new CreateUserRequest(email, password, fullName, phone, Role.CUSTOMER, null);
    }
}
