package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    // Репозиторий пользователей внедряется через конструктор
    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Найти в базе пользователя, от имени которого пришёл запрос.
     * Логин (email) берётся из Principal, который заполняет Spring Security после проверки Basic Auth.
     */
    public User from(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException(userRepository.notFoundMessage()));
    }
}
