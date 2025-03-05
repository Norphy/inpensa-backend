package com.orphy.inpensa_backend.v1.service;

import com.orphy.inpensa_backend.v1.data.SubCategoryRepository;
import com.orphy.inpensa_backend.v1.model.SubCategory;
import com.orphy.inpensa_backend.v1.model.dto.SubCategoryDto;
import com.orphy.inpensa_backend.v1.util.security.annotations.HasAnyWritePermission;
import com.orphy.inpensa_backend.v1.util.security.annotations.IsAdminRead;
import com.orphy.inpensa_backend.v1.util.security.annotations.IsAdminWrite;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    public SubCategoryService(SubCategoryRepository subCategoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
    }

    @IsAdminRead
    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.getAllSubCategories();
    }

    @PreAuthorize("@authz.isAdminReadOrCurrentUser(authentication, #userId)")
    public List<SubCategory> getAllSubCategoriesByUser(String userId) {
        return subCategoryRepository.getAllSubCategoriesByUser(userId);
    }


    @PostAuthorize("@authz.isAdminReadOrCurrentUser(authentication, returnObject.ownerId)")
    public SubCategory getSubCategoryById(UUID subCategoryId) {
        return subCategoryRepository.getSubCategoryById(subCategoryId);
    }

    @HasAnyWritePermission
    public UUID saveSubCategory(SubCategoryDto subCategoryDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String userId = auth.getName();
        SubCategory toBeSavedSubCat = new SubCategory(null, subCategoryDto.subCategory(), subCategoryDto.categoryId(), userId);
        SubCategory savedSubCat = saveSubCat(toBeSavedSubCat);
        return savedSubCat.id();
    }

    @IsAdminWrite
    public UUID saveSubCategory(String userId, SubCategoryDto subCategoryDto) {
        SubCategory toBeSavedSubCat = new SubCategory(null, subCategoryDto.subCategory(), subCategoryDto.categoryId(), userId);
        SubCategory savedSubCat = saveSubCat(toBeSavedSubCat);
        return savedSubCat.id();
    }

    @Transactional
    @PreAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, #subCategory.ownerId)")
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, returnObject)")
    public String update(SubCategory subCategory) {

        SubCategory toBeUpdated = subCategoryRepository.getSubCategoryById(subCategory.id());
        subCategoryRepository.updateSubCategory(subCategory);
        return toBeUpdated.ownerId();
    }

    @Transactional
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, returnObject)")
    public String delete(UUID subCategoryId) {

        SubCategory toBeUpdated = subCategoryRepository.getSubCategoryById(subCategoryId);
        subCategoryRepository.deleteSubCategory(subCategoryId);
        return toBeUpdated.ownerId();
    }

    private SubCategory saveSubCat(SubCategory subCategory) {
        return subCategoryRepository.saveSubCategory(subCategory);
    }
}
