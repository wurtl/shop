package com.example.demo.service;

import com.example.demo.DTO.CreateUserRequest;
import com.example.demo.DTO.PasswordChangeRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.Require;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Репозиторий пользователей и шифратор паролей внедряются через конструктор
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Все пользователи системы (для админки).
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Пользователь по ID; если не найден — 404.
     */
    public User getUserById(Long id) {
        return userRepository.getOrThrow(id);
    }

    /**
     * Единое создание пользователя: и саморегистрация, и создание сотрудника из админки.
     * Проверяет, что email свободен и роль указана; пароль сохраняется только в виде BCrypt-хеша.
     */
    public User createUser(CreateUserRequest req) {
        Require.notBlank(req.email(), "Не указан email");
        Require.notBlank(req.password(), "Не указан пароль");
        Require.notNull(req.role(), "Не указана роль");
        if (userRepository.existsByEmail(req.email())) {
            throw new BusinessException("Email уже занят");
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName());
        user.setPhone(req.phone());
        user.setRole(req.role());
        user.setTransport(req.transport());
        return userRepository.save(user);
    }

    /**
     * Сменить роль пользователя (допустимые роли ограничены enum Role).
     */
    public User changeRole(Long id, Role newRole) {
        Require.notNull(newRole, "Не указана роль");
        User user = getUserById(id);
        user.setRole(newRole);
        return userRepository.save(user);
    }

    /**
     * Удалить пользователя; если не найден — 404.
     */
    public void deleteUser(Long id) {
        userRepository.delete(getUserById(id));
    }

    /**
     * Сменить пароль: сначала сверяем старый с хешем в базе, потом сохраняем хеш нового.
     */
    public void changePassword(User user, PasswordChangeRequest req) {
        Require.notBlank(req.newPassword(), "Не указан новый пароль");
        if (!passwordEncoder.matches(req.oldPassword(), user.getPassword())) {
            throw new BusinessException("Неверный текущий пароль");
        }
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
    }

    /**
     * Начать или закончить рабочую смену (для флориста и курьера).
     */
    public User setOnShift(User user, boolean onShift) {
        user.setOnShift(onShift);
        return userRepository.save(user);
    }
}
