package com.wawa87.moneystack.category.service;

import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public class CategoryService {
    private CategoryDAO categoryDAO;
    private AuthorizationService authorizationService;

    public CategoryService(CategoryDAO categoryDAO, AuthorizationService authorizationService) {
        this.categoryDAO = categoryDAO;
        this.authorizationService = authorizationService;
    }

    public Category saveCategory(Category category) {
        Optional<Category> categoryOptional = this.categoryDAO.save(category);
        category = categoryOptional.get();
        return category;
    }

    public Category saveCategory(Long userId, Category category) throws BadRequestException {
        Category newCategory = new Category();
        newCategory.setUserId(userId);
        newCategory.setName(category.getName());
        newCategory.setDescription(category.getDescription());
        Optional<Category> categoryOpt = this.categoryDAO.save(newCategory);
        if (categoryOpt.isEmpty()) throw new BadRequestException("Category failed to save.");
        else return categoryOpt.get();
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

    public Category findCategoryById(Long categoryId, Long requestedById) throws IllegalAccessException, NotFoundException {
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isEmpty()) throw new NotFoundException();
        if (!this.authorizationService.authorizeForCategory(requestedById, categoryOpt.get())) throw new IllegalAccessException("Forbidden.");
        return categoryOpt.get();
    }

    public void updateCategory(Category category) {
        this.categoryDAO.update(category);
    }

    public Category updateCategory(Long categoryId, Long requesterId, Category category) throws NotFoundException, ValidationException, BadRequestException {
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isEmpty()) throw new NotFoundException();
        if (!this.authorizationService.authorizeForCategory(requesterId, categoryOpt.get())) throw new ValidationException();

        // Update the Category.
        Category updateCategory = categoryOpt.get();
        updateCategory.setName(category.getName());
        updateCategory.setDescription(category.getDescription());

        // Return the result code. Success == 1, Error == 0.
        if (categoryDAO.update(updateCategory) == 1) return updateCategory;
        else throw new BadRequestException("Category update failed.");
    }

    public void deleteCategoryById(Long categoryId, Long requesterId) throws NotFoundException, ValidationException, BadRequestException {
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isEmpty()) throw new NotFoundException();
        if (!this.authorizationService.authorizeForCategory(requesterId, categoryOpt.get())) throw new ValidationException();

        if (categoryDAO.deleteById(categoryId) != 1) throw new BadRequestException("Category delete failed.");
    }

    public int deleteCategory(Category category) {
        return categoryDAO.deleteById(category.getId());
    }
}
