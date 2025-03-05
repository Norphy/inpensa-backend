package com.orphy.inpensa_backend.v1.model;

import java.util.UUID;

public record Category(UUID id, String category, String ownerId) {
    public Category getCopyWithId(UUID id) {
        return new Category(id, this.category, this.ownerId);
    }
}
