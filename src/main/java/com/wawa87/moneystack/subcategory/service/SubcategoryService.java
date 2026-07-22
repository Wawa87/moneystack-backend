package com.wawa87.moneystack.subcategory.service;

import com.wawa87.moneystack.subcategory.dao.SubcategoryDAO;
import com.wawa87.moneystack.subcategory.dao.SubcategoryDTO;
import com.wawa87.moneystack.subcategory.model.Subcategory;

public class SubcategoryService {
    SubcategoryDAO subcategoryDAO;

    public SubcategoryService(SubcategoryDAO subcategoryDAO) {
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
}
