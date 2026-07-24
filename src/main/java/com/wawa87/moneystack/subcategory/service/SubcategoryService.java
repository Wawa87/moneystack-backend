package com.wawa87.moneystack.subcategory.service;

import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.subcategory.model.Subcategory;

import java.util.List;

public interface SubcategoryService {
    public Subcategory save(Long requesterId, Subcategory subcategory) throws BadRequestException;
    public List<Subcategory> getAll(Long requesterId);
    public Subcategory findById(Long requesterId, Long subcategoryId);
    public Subcategory update(Long requesterId, Long subcategoryId, Subcategory subcategory);
    public void delete(Long requesterId, Long subcategoryId);
}
