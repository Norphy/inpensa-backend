package com.orphy.inpensa_backend.model;

import java.util.UUID;

public record Wallet(UUID id,
                     String name,
                     long dateCreated,
                     long amount,
                     String ownerId) {
    public Wallet copyWithId(UUID id) {
        return new Wallet(id, this.name, this.dateCreated, this.amount, this.ownerId);
    }
}