package com.example.demo.DTO;

import com.example.demo.entity.Role;

/** Создание пользователя: и при саморегистрации, и из админки. */
public record CreateUserRequest(
        String email,
        String password,
        String fullName,
        String phone,
        Role role,
        String transport
) {}
