package com.wawa87.moneystack.subcategory.service;

import com.wawa87.moneystack.auth.service.AuthorizationService;
import com.wawa87.moneystack.common.db.ServletUtility;
import com.wawa87.moneystack.common.exceptions.BadRequestException;
import com.wawa87.moneystack.common.exceptions.NotFoundException;
import com.wawa87.moneystack.common.exceptions.ValidationException;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDTO;
import com.wawa87.moneystack.subcategory.model.Subcategory;

import java.util.List;
import java.util.Optional;

public class SubcategoryServiceImpl implements SubcategoryService {
    SubcategoryDAO subcategoryDAO;
    AuthorizationService authorizationService;

    public SubcategoryServiceImpl(SubcategoryDAO subcategoryDAO, AuthorizationService authorizationService) {
        this.subcategoryDAO = subcategoryDAO;
        this.authorizationService = authorizationService;
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
    public Subcategory findById(Long requesterId, Long subcategoryId) throws ValidationException, NotFoundException {
        // Authorize.
        if (!authorizationService.authorizeForSubcategory(requesterId, subcategoryId)) throw new ValidationException();

        // Get the Subcategory.
        Optional<Subcategory> subcategoryOpt = subcategoryDAO.findById(subcategoryId);

        if (subcategoryOpt.isEmpty()) throw new NotFoundException();
        return subcategoryOpt.get();
    }

    @Override
    public List<Subcategory> findByCategoryId(Long requesterId, Long categoryId) throws NotFoundException, ValidationException {
        // Authorize.
        if (!authorizationService.authorizeForCategory(requesterId, categoryId)) throw new ValidationException();

        // Get the Subcategories.
        List<Subcategory> subcategories = subcategoryDAO.findByCategoryId(categoryId);
        if (subcategories.isEmpty()) throw new NotFoundException();
        else return subcategories;
    }

    @Override
    public Subcategory update(Long requesterId, Long subcategoryId, Subcategory subcategory) throws ValidationException, NotFoundException, BadRequestException {
        // Authorize.
        if (!authorizationService.authorizeForSubcategory(requesterId, subcategoryId)) throw new ValidationException();

        // Get the Subcategory to update.
        Optional<Subcategory> subcategoryOpt = subcategoryDAO.findById(subcategoryId);
        if (subcategoryOpt.isEmpty()) throw new NotFoundException();

        Subcategory subcategoryToUpdate = subcategoryOpt.get();
        subcategoryToUpdate.setName(subcategory.getName());
        subcategoryToUpdate.setDescription(subcategory.getDescription());

        // Return the result.
        int result = subcategoryDAO.update(subcategoryToUpdate);
        if (result == 1) return subcategoryToUpdate;
        else throw new BadRequestException("Failed to update the Subcategory.");
    }

    @Override
    public void delete(Long requesterId, Long subcategoryId) throws ValidationException, NotFoundException, BadRequestException {
        // Authorize.
        if (!authorizationService.authorizeForSubcategory(requesterId, subcategoryId)) throw new ValidationException();

        // Get the requested Subcategory.
        Optional<Subcategory> subcategoryOpt = subcategoryDAO.findById(subcategoryId);
        if (subcategoryOpt.isEmpty()) throw new NotFoundException();

        // Delete the Subcategory.
        int result = subcategoryDAO.deleteById(subcategoryId);
        if (result == 0) throw new BadRequestException();
    }
}
