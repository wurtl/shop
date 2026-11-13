package com.example.demo.controller;

import com.example.demo.DTO.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.web.CurrentUser;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Общая часть кабинетов сотрудников: просмотр собственного профиля.
 * Класс абстрактный и сам не регистрируется как контроллер: URL-префикс (/api/admin, /api/florist, ...)
 * задаёт наследник через @RequestMapping, а метод profile() достаётся ему «в наследство».
 */
public abstract class ProfileController {

    /**
     * Получить свой профиль.
     * Пользователя подставляет @CurrentUser, возвращаем UserResponse — без хеша пароля.
     */
    @GetMapping("/profile")
    public UserResponse profile(@CurrentUser User user) {
        return UserResponse.from(user);
    }
}
