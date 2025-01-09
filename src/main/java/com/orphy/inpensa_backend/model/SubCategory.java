package com.orphy.inpensa_backend.model;

import java.util.UUID;

public record SubCategory(UUID id, String subCategory, UUID categoryId, String userId) {
    public SubCategory copyWithId(UUID id) {
        return new SubCategory(id, this.subCategory, this.categoryId, this.userId);
    }
}