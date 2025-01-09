package com.orphy.inpensa_backend.service;

import com.orphy.inpensa_backend.model.SubCategory;

import java.util.List;
import java.util.UUID;

public interface SubCategoryService {

    public List<SubCategory> getAllSubCategories(String userId);

    public Object getByIdAndUser(UUID objectId, String userId);

    public UUID save(ObjectDto objectDto);

    public void update(UUID id,  object);

    public String delete(UUID objectId);
}
