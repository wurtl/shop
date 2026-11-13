package com.example.demo.service;

import com.example.demo.entity.Favorite;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    // Репозитории избранного и товаров внедряются через конструктор
    public FavoriteService(FavoriteRepository favoriteRepository, ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
    }

    /**
     * Избранные товары пользователя.
     */
    @Transactional(readOnly = true)
    public List<Favorite> getUserFavorites(User user) {
        return favoriteRepository.findByCustomerId(user.getId());
    }

    /**
     * Добавить товар в избранное. Если он уже там — ошибка 400, если товара нет — 404.
     */
    public Favorite addFavorite(User user, Long productId) {
        if (favoriteRepository.findByCustomerIdAndProductId(user.getId(), productId).isPresent()) {
            throw new BusinessException("Товар уже в избранном");
        }
        Favorite favorite = new Favorite();
        favorite.setCustomer(user);
        favorite.setProduct(productRepository.getOrThrow(productId));
        return favoriteRepository.save(favorite);
    }

    /**
     * Убрать товар из избранного. Если его там не было — ничего не делает.
     */
    public void removeFavorite(User user, Long productId) {
        favoriteRepository.findByCustomerIdAndProductId(user.getId(), productId)
                .ifPresent(favoriteRepository::delete);
    }
}
