package com.example.demo.entity;

/** Сущность, принадлежащая покупателю: общая проверка владельца. */
public interface CustomerOwned {

    User getCustomer();

    /**
     * Принадлежит ли сущность этому пользователю (сравнение по ID покупателя).
     */
    default boolean isOwnedBy(User user) {
        return getCustomer().getId().equals(user.getId());
    }
}
