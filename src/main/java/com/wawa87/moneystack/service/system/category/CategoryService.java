package com.wawa87.moneystack.service.system.category;

import com.wawa87.moneystack.service.auth.AuthorizationChecker;
import com.wawa87.moneystack.service.system.category.dao.CategoryDAO;
import com.wawa87.moneystack.service.system.category.dao.CategoryDTO;
import com.wawa87.moneystack.service.system.category.model.Category;
import com.wawa87.moneystack.service.system.db.ResultStatus;

import javax.xml.transform.Result;
import java.io.FileNotFoundException;
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

    public Category saveCategory(Long userId, CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setUserId(userId);
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        Optional<Category> categoryOpt = this.categoryDAO.save(category);
        if (!categoryOpt.isPresent()) return null;
        else return categoryOpt.get();
    }

    public void updateCategory(Category category) {
        this.categoryDAO.update(category);
    }

    public List<Category> getCategoriesByUsername(String username) {
        return categoryDAO.findByUsername(username);
    }

    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryDAO.findByUserId(userId);
    }

    public Category findCategoryById(Long categoryId) {
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isEmpty()) return null;
        return categoryOpt.get();
    }

    public Category findCategoryById(Long categoryId, Long userId) throws IllegalAccessException {
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isEmpty()) return null;
        if (!AuthorizationChecker.authorizeCategory(categoryOpt.get(), userId)) throw new IllegalAccessException("Forbidden.");
        else return categoryOpt.get();
    }

    public ResultStatus updateCategory(Long categoryId, Long userId, CategoryDTO categoryDTO) {
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isEmpty()) return ResultStatus.NOT_FOUND; // Return not found error.
        if (!AuthorizationChecker.authorizeCategory(categoryOpt.get(), userId)) return ResultStatus.FORBIDDEN;

        // Update the Category.
        Category category = categoryOpt.get();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        // Return the result code. Success == 1, Error == 0.
        int result = categoryDAO.update(category); // Returns 1 for row updated. Returns 0 for error/no rows updated.
        if (result == 1) return ResultStatus.SUCCESS;
        else return ResultStatus.ERROR;
    }

    public ResultStatus deleteCategoryById(Long categoryId, Long userId) {
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isEmpty()) return ResultStatus.NOT_FOUND; // Return not found error.
        if (!AuthorizationChecker.authorizeCategory(categoryOpt.get(), userId)) return ResultStatus.FORBIDDEN;

        int result = categoryDAO.deleteById(categoryId);
        if (result == 1) return ResultStatus.SUCCESS;
        else return ResultStatus.ERROR;
    }

    public int deleteCategory(Category category) {
        return categoryDAO.deleteById(category.getId());
    }
}
