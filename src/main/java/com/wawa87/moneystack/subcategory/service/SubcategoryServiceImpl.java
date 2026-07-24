package com.wawa87.moneystack.subcategory.service;

import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDTO;
import com.wawa87.moneystack.subcategory.model.Subcategory;

import java.util.List;
import java.util.Optional;

public class SubcategoryServiceImpl implements SubcategoryService {
    SubcategoryDAO subcategoryDAO;

    public SubcategoryServiceImpl(SubcategoryDAO subcategoryDAO) {
        this.subcategoryDAO = subcategoryDAO;
    }

    public SubcategoryDTO getSubcategoryDTOById(Long subcategoryId) {
        Subcategory subcategory = subcategoryDAO.findById(subcategoryId).get();
        SubcategoryDTO subcategoryDTO = new SubcategoryDTO();
        subcategoryDTO.setId(subcategory.getId());
        subcategoryDTO.setName(subcategory.getName());
        subcategoryDTO.setDescription(subcategory.getDescription());
        return subcategoryDTO;
    }

    public void saveSubcategory(Subcategory subcategory) {
        subcategoryDAO.save(subcategory);
    }

    @Override
    public Subcategory save(Long requesterId, Subcategory subcategory) throws BadRequestException {
        // Require parent Category Id.
        if (subcategory.getCategoryId() == null || subcategory.getCategoryId() == 0) throw new BadRequestException("Parent Category Id required.");

        // Require Name value.
        if (subcategory.getName().isBlank()) throw new BadRequestException("Subcategory name is required.");

        // Require description value.
        if (subcategory.getDescription().isBlank()) throw new BadRequestException("Subcategory description is required.");

        // Attempt to save.
        Optional<Subcategory> subcategoryOpt = subcategoryDAO.save(subcategory);

        if (subcategoryOpt.isEmpty()) throw new BadRequestException("Failed to save the Subcategory: " + ServletUtility.gson.toJson(subcategory));
        return subcategoryOpt.get();
    }

    @Override
    public List<Subcategory> getAll(Long requesterId) {
        return List.of();
    }

    @Override
    public Subcategory findById(Long requesterId, Long subcategoryId) {
        return null;
    }

    @Override
    public Subcategory update(Long requesterId, Long subcategoryId, Subcategory subcategory) {
        return null;
    }

    @Override
    public void delete(Long requesterId, Long subcategoryId) {

    }
}
