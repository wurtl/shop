package com.example.demo.DTO;

import com.example.demo.entity.Role;

/**
 * Запрос «сменить роль пользователя»: новая роль (одна из значений enum Role).
 */
public record RoleRequest(Role role) {}
