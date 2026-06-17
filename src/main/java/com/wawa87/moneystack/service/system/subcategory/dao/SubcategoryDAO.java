package com.wawa87.moneystack.service.system.subcategory.dao;

import com.wawa87.moneystack.service.system.subcategory.model.Subcategory;

import java.util.List;
import java.util.Optional;

public interface SubcategoryDAO {
    // Create
    Optional<Subcategory> save(Subcategory subcategory);

    // Read
    Optional<Subcategory> findById(Long id);
    List<Subcategory> findByCategoryId(Long categoryId);

    // Update
    int update(Subcategory subcategory);

    // Delete
    int deleteById(Long id);
}
