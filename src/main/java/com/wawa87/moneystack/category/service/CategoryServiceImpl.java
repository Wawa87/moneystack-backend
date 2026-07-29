package com.wawa87.moneystack.category.service;

import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.category.dao.CategoryDAO;
import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDAO categoryDAO;
    private AuthorizationService authorizationService;

    public CategoryServiceImpl(CategoryDAO categoryDAO, AuthorizationService authorizationService) {
        this.categoryDAO = categoryDAO;
        this.authorizationService = authorizationService;
    }

    @Override
    public Category save(Long requesterId, Category category) throws ValidationException, BadRequestException {
        // Validate Cateogry values.
        if (category.getName().isBlank()) throw new BadRequestException("Category name is invalid.");
        if (category.getDescription().isBlank()) throw new BadRequestException("Category description is invalid.");

        // Set the User Id.
        category.setUserId(requesterId);

        // Save the new Category.
        Optional<Category> categoryOpt = this.categoryDAO.save(category);
        if (categoryOpt.isEmpty()) throw new BadRequestException("Category failed to save.");
        else return categoryOpt.get();
    }

    @Override
    public List<Category> getAll(Long requesterId) {
        return categoryDAO.findByUserId(requesterId);
    }

    @Override
    public Category findById(Long requesterId, Long categoryid) throws ValidationException, NotFoundException {
        // Authorize.
        if (!authorizationService.authorizeForCategory(requesterId, categoryid)) throw new ValidationException();

        // Get the Categories.
        Optional<Category> categoryOpt = categoryDAO.findById(categoryid);
        if (categoryOpt.isEmpty()) throw new NotFoundException();
        return categoryOpt.get();
    }

    @Override
    public Category update(Long requesterId, Long categoryId, Category category) throws ValidationException, NotFoundException, BadRequestException {
        // Authorize.
        if (!authorizationService.authorizeForCategory(requesterId, categoryId)) throw new ValidationException();

        // Get the Category to update.
        Optional<Category> categoryOpt = categoryDAO.findById(categoryId);
        if (categoryOpt.isEmpty()) throw new NotFoundException();

        // Update the Category.
        Category updateCategory = categoryOpt.get();
        updateCategory.setName(category.getName());
        updateCategory.setDescription(category.getDescription());

        // Return the result code. Success == 1, Error == 0.
        if (categoryDAO.update(updateCategory) == 1) return updateCategory;
        else throw new BadRequestException("Category update failed.");
    }

    @Override
    public void delete(Long requesterId, Long categoryId) throws ValidationException, BadRequestException {
        // Authorize.
        if (!authorizationService.authorizeForCategory(requesterId, categoryId)) throw new ValidationException();

        // Delete the Category.
        if (categoryDAO.deleteById(categoryId) != 1) throw new BadRequestException("Failed to delete the Category.");
    }
}
