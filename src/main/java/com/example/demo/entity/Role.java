package com.example.demo.entity;

/**
 * Роли пользователей: покупатель, флорист, курьер, администратор.
 */
public enum Role {
    CUSTOMER, FLORIST, COURIER, ADMIN;

    /**
     * Имя роли в формате Spring Security (ROLE_ADMIN и т.д.).
     */
    public String authority() {
        return "ROLE_" + name();
    }
}
