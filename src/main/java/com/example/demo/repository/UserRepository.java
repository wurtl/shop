package com.example.demo.repository;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Пользователи всех ролей.
 */
public interface UserRepository extends BaseRepository<User> {

    // Для аутентификации
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Для админки (списки сотрудников по ролям)
    List<User> findByRole(Role role);

    @Override
    default String notFoundMessage() { return "Пользователь не найден"; }
}
