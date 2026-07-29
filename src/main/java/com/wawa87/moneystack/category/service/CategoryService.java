package com.wawa87.moneystack.category.service;

import com.wawa87.moneystack.category.model.Category;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;

import java.util.List;

public interface CategoryService {
    public Category save(Long requesterId, Category category) throws ValidationException, BadRequestException;
    public List<Category> getAll(Long requesterId);
    public Category findById(Long requesterId, Long categoryid) throws ValidationException, NotFoundException;
    public Category update(Long requesterId, Long categoryId, Category category) throws ValidationException, NotFoundException, BadRequestException;
    public void delete(Long requesterId, Long categoryId) throws ValidationException, BadRequestException;
}
