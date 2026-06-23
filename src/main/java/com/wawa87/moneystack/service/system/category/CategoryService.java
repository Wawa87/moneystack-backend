package com.wawa87.moneystack.service.system.category;

import com.wawa87.moneystack.service.system.category.dao.CategoryDAO;
import com.wawa87.moneystack.service.system.category.dao.CategoryDTO;
import com.wawa87.moneystack.service.system.category.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryService {
    private CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public Category saveCategory(Category category) {
        Optional<Category> categoryOptional = this.categoryDAO.save(category);
        category = categoryOptional.get();
        return category;
    }

    public List<CategoryDTO> getCategories(String username) {
        List<Category> categories = categoryDAO.findByUsername(username);
        List<CategoryDTO> categoriesDTO = new ArrayList<>();
        categories.forEach((item) -> {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(item.getId());
            categoryDTO.setUserId(item.getUserId());
            categoryDTO.setName(item.getName());

            categoriesDTO.add(categoryDTO);
        });
        return categoriesDTO;
    }

    public CategoryDTO getCategoryDTOById(Long categoryId) {
        Category category = categoryDAO.findById(categoryId).get();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setUserId(category.getUserId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        return categoryDTO;
    }

    public int deleteCategory(Category category) {
        return categoryDAO.deleteById(category.getId());
    }
}
