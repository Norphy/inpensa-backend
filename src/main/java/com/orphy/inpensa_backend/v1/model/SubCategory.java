package com.orphy.inpensa_backend.v1.model;

import java.util.UUID;

public record SubCategory(UUID id, String subCategory, UUID categoryId, String ownerId) {
    public SubCategory copyWithId(UUID id) {
        return new SubCategory(id, this.subCategory, this.categoryId, this.ownerId);
    }
}