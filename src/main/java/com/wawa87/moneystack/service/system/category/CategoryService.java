package com.wawa87.moneystack.service.system.category;

import com.wawa87.moneystack.service.system.category.dao.CategoryDAO;
import com.wawa87.moneystack.service.system.category.dao.CategoryDTO;
import com.wawa87.moneystack.service.system.category.model.Category;
import de.mkammerer.argon2.Argon2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryService {
    private CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public Category addCategory(Category category) {
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
            categoryDTO.setCategoryName(item.getCategoryName());
            categoryDTO.setCreatedAt(item.getCreatedAt().toString());

            categoriesDTO.add(categoryDTO);
        });
        return categoriesDTO;
    }

    public int deleteCategory(Category category) {
        return categoryDAO.deleteById(category.getId());
    }
}
