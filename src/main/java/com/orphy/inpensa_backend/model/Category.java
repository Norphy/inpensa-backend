package com.orphy.inpensa_backend.model;

import java.util.UUID;

public record Category(UUID id, String category, String userId) {
    public Category getCopyWithId(UUID id) {
        return new Category(id, this.category, this.userId);
    }
}
