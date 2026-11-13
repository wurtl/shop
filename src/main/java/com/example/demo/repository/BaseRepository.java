package com.example.demo.repository;

import com.example.demo.entity.BaseEntity;
import com.example.demo.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;


@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    /** Текст ошибки для конкретной сущности; переопределяется в наследнике. */
    default String notFoundMessage() {
        return "Запись не найдена";
    }

    /**
     * Найти запись по ID или бросить NotFoundException (HTTP 404) с текстом из notFoundMessage().
     * Для id = null тоже бросает NotFoundException.
     */
    default T getOrThrow(Long id) {
        // id == null (например, в теле запроса не передали productId) тоже считаем «не найдено», а не ошибкой 500
        return Optional.ofNullable(id).flatMap(this::findById)
                .orElseThrow(() -> new NotFoundException(notFoundMessage()));
    }
}
