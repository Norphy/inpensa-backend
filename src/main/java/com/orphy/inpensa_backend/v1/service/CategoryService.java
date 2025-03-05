package com.orphy.inpensa_backend.v1.service;

import com.orphy.inpensa_backend.v1.data.CategoryRepository;
import com.orphy.inpensa_backend.v1.model.Category;
import com.orphy.inpensa_backend.v1.model.dto.CategoryDto;
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
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @IsAdminRead
    public List<Category> getAll() {
        return categoryRepository.getAllCategories();
    }

    @PreAuthorize("@authz.isAdminReadOrCurrentUser(authentication, #userId)")
    public List<Category> getCategoriesByUser(String userId) {
        return categoryRepository.getAllCategoriesByUser(userId);
    }

    @PostAuthorize("@authz.isAdminReadOrCurrentUser(authentication, returnObject.ownerId)")
    public Category getCategoryById(UUID categoryId) {
        return categoryRepository.getCategoryById(categoryId);
    }

    @HasAnyWritePermission
    public UUID saveCategory(CategoryDto categoryDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String userId = auth.getName();
        final Category category = new Category(null, categoryDto.category(), userId);
        final Category savedCat = categoryRepository.saveCategory(category);
        return savedCat.id();
    }

    @IsAdminWrite
    public UUID saveCategory(CategoryDto categoryDto, String userId) {
        final Category category = new Category(null, categoryDto.category(), userId);
        final Category savedCat = categoryRepository.saveCategory(category);
        return savedCat.id();
    }

    @Transactional
    @PreAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, #category.ownerId)")
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, returnObject)")
    @SuppressWarnings("UnusedReturnValue")
    public String updateCategory(Category category) {
        final Category toBeUpdatedCat = categoryRepository.getCategoryById(category.id());
        categoryRepository.updateCategory(category);
        return toBeUpdatedCat.ownerId();
    }

    @Transactional
    @PostAuthorize("@authz.isAdminWriteOrCurrentUser(authentication, returnObject)")
    public String delete(UUID categoryId) {
        final Category toBeDeleted = categoryRepository.getCategoryById(categoryId);
        categoryRepository.deleteCategory(categoryId);
        return toBeDeleted.ownerId();
    }
}
