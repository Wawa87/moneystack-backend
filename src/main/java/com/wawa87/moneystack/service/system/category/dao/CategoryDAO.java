package com.wawa87.moneystack.service.system.category.dao;

import com.wawa87.moneystack.service.system.category.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    // Create
    Optional<Category> save(Category category);

    // Read
    Optional<Category> findById(Long id);
    List<Category> findByName(String name);
    List<Category> findByUserId(Long user_id);
    List<Category> findByNameAndUserId(String name, Long user_id);
    List<Category> findByUsername(String username);
    List<Category> findByNameAndUsername(String name, String username);

    // Update
    int update(Category category);

    // Delete
    int deleteById(Long id);
}
