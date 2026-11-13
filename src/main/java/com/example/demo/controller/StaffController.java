package com.example.demo.controller;

import com.example.demo.DTO.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.web.CurrentUser;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Профиль и рабочие смены — общие для флориста и курьера.
 * Наследуется от ProfileController (профиль) и добавляет начало/конец смены,
 * чтобы эти методы не дублировались в FloristController и CourierController.
 */
public abstract class StaffController extends ProfileController {

    private final UserService userService;

    protected StaffController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Начать рабочую смену (флаг onShift = true).
     * Возвращает обновлённый профиль.
     */
    @PostMapping("/shift/start")
    public UserResponse startShift(@CurrentUser User user) {
        return UserResponse.from(userService.setOnShift(user, true));
    }

    /**
     * Закончить рабочую смену (флаг onShift = false).
     * Возвращает обновлённый профиль.
     */
    @PostMapping("/shift/end")
    public UserResponse endShift(@CurrentUser User user) {
        return UserResponse.from(userService.setOnShift(user, false));
    }
}
