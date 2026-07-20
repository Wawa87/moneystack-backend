package com.wawa87.moneystack.category.dao;

import com.wawa87.moneystack.category.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    // Create
    Optional<Category> save(Category category);

    // Read
    Optional<Category> findById(Long id);
    List<Category> findByName(String name);
    List<Category> findByUserId(Long userId);
    List<Category> findByNameAndUserId(String name, Long userId);
    List<Category> findByUsername(String username);

    // Update
    int update(Category category);

    // Delete
    int deleteById(Long id);
}
