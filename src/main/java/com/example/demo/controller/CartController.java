package com.example.demo.controller;

import com.example.demo.DTO.CartItemRequest;
import com.example.demo.DTO.QuantityRequest;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.User;
import com.example.demo.service.CartService;
import com.example.demo.web.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Получить содержимое корзины текущего пользователя.
     * Благодаря @CurrentUser мы получаем только корзину этого пользователя,
     * не давая возможности подсмотреть чужие товары.
     */
    @GetMapping
    public List<CartItem> getCart(@CurrentUser User user) {
        return cartService.getCart(user);
    }

    /**
     * Добавить товар в корзину.
     * В теле запроса (CartItemRequest) передается productId и quantity.
     * Сервис сам проверит, есть ли уже такой товар: если да, увеличит количество, если нет — создаст.
     */
    @PostMapping("/items")
    public CartItem addItem(@CurrentUser User user, @RequestBody CartItemRequest request) {
        return cartService.addItem(user, request);
    }

    /**
     * Изменить количество конкретной позиции в корзине.
     * itemId берется из пути URL, новое количество — из DTO QuantityRequest.
     */
    @PatchMapping("/items/{itemId}")
    public CartItem updateQuantity(@CurrentUser User user, @PathVariable Long itemId,
                                   @RequestBody QuantityRequest request) {
        return cartService.updateQuantity(user, itemId, request.quantity());
    }

    /**
     * Удалить одну позицию из корзины по её ID.
     * Возвращает статус 204 No Content — значит "успешно удалено, отвечать нечем".
     */
    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@CurrentUser User user, @PathVariable Long itemId) {
        cartService.removeItem(user, itemId);
    }

    /**
     * Полностью очистить корзину пользователя (например, после оформления заказа).
     * Также возвращает 204 No Content.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@CurrentUser User user) {
        cartService.clearCart(user);
    }
}
