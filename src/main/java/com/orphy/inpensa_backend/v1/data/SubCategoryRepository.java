package com.orphy.inpensa_backend.v1.data;

import com.orphy.inpensa_backend.v1.model.SubCategory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubCategoryRepository {

    public List<SubCategory> getAllSubCategories();

    public List<SubCategory> getAllSubCategoriesByUser(String userId);

    public SubCategory getSubCategoryById(UUID subCatId);

//    public SubCategory getSubCategoryByIdAndUser(UUID subCatId, String userId);

    public SubCategory saveSubCategory(SubCategory subCategory);

    public void updateSubCategory(SubCategory subCategory);

    public void deleteSubCategory(UUID subCatId);
}
