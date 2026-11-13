package com.example.demo.DTO;

/**
 * Запрос «сменить пароль»: текущий и новый пароль.
 */
public record PasswordChangeRequest(String oldPassword, String newPassword) {}
