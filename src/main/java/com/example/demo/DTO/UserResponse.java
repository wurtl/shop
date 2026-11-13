package com.example.demo.DTO;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;

/**
 * Пользователь для ответов клиенту. В отличие от сущности User не содержит хеш пароля.
 */
public record UserResponse(
        Long id,
        String email,
        String fullName,
        String phone,
        Role role,
        String transport,
        Boolean onShift
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getEmail(), user.getFullName(),
                user.getPhone(), user.getRole(), user.getTransport(), user.getOnShift()
        );
    }
}
