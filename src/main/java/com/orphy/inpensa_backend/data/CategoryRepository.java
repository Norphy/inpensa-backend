package com.orphy.inpensa_backend.data;

import com.orphy.inpensa_backend.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository {

    public List<Category> getAllCategories();

    public List<Category> getAllCategoriesByUser(String userId);

    public Category getCategoryById(UUID catId);

    public Category getCategoryByIdAndUser(UUID catId, String userId);

    public Category saveCategory(Category category);

    public void updateCategory(Category category);

    public void deleteCategory(UUID catId);
}
