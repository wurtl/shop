package com.example.demo.controller;

import com.example.demo.DTO.MessageResponse;
import com.example.demo.DTO.RegisterRequest;
import com.example.demo.DTO.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.web.CurrentUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    // Внедряем сервис пользователей через конструктор
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Регистрация нового пользователя.
     * Принимает DTO RegisterRequest (email, пароль, имя).
     * Метод req.asCustomer() конвертирует DTO в сущность User и ставит роль CUSTOMER,
     * чтобы злоумышленник не мог зарегистрироваться как ADMIN.
     */
    @PostMapping("/register")
    public MessageResponse register(@RequestBody RegisterRequest req) {
        userService.createUser(req.asCustomer());
        return new MessageResponse("Успешная регистрация");
    }

    /**
     * Получить данные текущего авторизованного пользователя.
     * Аннотация @CurrentUser сама находит пользователя в БД по логину из заголовка Basic Auth.
     * Возвращаем UserResponse (без пароля).
     */
    @GetMapping("/me")
    public UserResponse me(@CurrentUser User user) {
        return UserResponse.from(user);
    }
}
