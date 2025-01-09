package com.orphy.inpensa_backend.service;

import com.orphy.inpensa_backend.model.Category;
import com.orphy.inpensa_backend.model.dto.CategoryDto;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    public List<Category> getAll(String userId);

    public Category getCategoryByIdAndUser(UUID categoryId, String userId);

    public UUID save(CategoryDto categoryDto);

    public void update(UUID id, Category category);

    public String delete(UUID categoryId);
}
