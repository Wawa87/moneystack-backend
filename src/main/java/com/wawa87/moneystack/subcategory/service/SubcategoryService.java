package com.wawa87.moneystack.subcategory.service;

import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.subcategory.model.Subcategory;

import java.util.List;

public interface SubcategoryService {
    public Subcategory save(Long requesterId, Subcategory subcategory) throws BadRequestException;
    public Subcategory findById(Long requesterId, Long subcategoryId) throws ValidationException, NotFoundException;
    public List<Subcategory> findByCategoryId(Long requesterId, Long categoryId) throws NotFoundException, ValidationException;
    public Subcategory update(Long requesterId, Long subcategoryId, Subcategory subcategory) throws ValidationException, NotFoundException, BadRequestException;
    public void delete(Long requesterId, Long subcategoryId) throws ValidationException, NotFoundException, BadRequestException;
}
