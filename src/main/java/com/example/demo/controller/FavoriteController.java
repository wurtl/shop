package com.example.demo.controller;

import com.example.demo.entity.Favorite;
import com.example.demo.entity.User;
import com.example.demo.service.FavoriteService;
import com.example.demo.web.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер избранных товаров покупателя.
 * Доступен только роли CUSTOMER (см. SecurityConfig); у каждого покупателя свой список.
 */
@RestController
@RequestMapping("/api/customer/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // Внедряем сервис избранного через конструктор
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Получить список избранного текущего покупателя.
     */
    @GetMapping
    public List<Favorite> getFavorites(@CurrentUser User user) {
        return favoriteService.getUserFavorites(user);
    }

    /**
     * Добавить товар в избранное.
     * productId берётся из URL. Если товар уже в избранном — вернётся ошибка 400.
     */
    @PostMapping("/{productId}")
    public Favorite addFavorite(@CurrentUser User user, @PathVariable Long productId) {
        return favoriteService.addFavorite(user, productId);
    }

    /**
     * Убрать товар из избранного.
     * Возвращает 204 No Content; если товара в избранном не было — ничего не происходит.
     */
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(@CurrentUser User user, @PathVariable Long productId) {
        favoriteService.removeFavorite(user, productId);
    }
}
