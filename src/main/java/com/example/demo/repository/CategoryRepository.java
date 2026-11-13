package com.example.demo.repository;

import com.example.demo.entity.Category;

import java.util.List;

/**
 * Категории товаров (дерево: у подкатегории есть родитель).
 */
public interface CategoryRepository extends BaseRepository<Category> {
    // Категории верхнего уровня (без родителя) для меню
    List<Category> findByParentIsNull();

    @Override
    default String notFoundMessage() { return "Категория не найдена"; }
}
