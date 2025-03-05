package com.orphy.inpensa_backend.v1.model;

import jakarta.annotation.Nonnull;

import java.util.UUID;

public record Transaction(UUID id,
                          long dateCreated,
                          long occurrenceDate,
                          String description,
                          int amount,
                          @Nonnull TransactionType type,
                          @Nonnull String tag, //TODO This should be another table?
                          @Nonnull UUID categoryId,
                          @Nonnull UUID subCategoryId,
                          @Nonnull UUID wallet, // TODO connect to wallet
                          @Nonnull String ownerId
) {
    public Transaction getCopyWithId(UUID id) {
        return new Transaction(id, this.dateCreated, this.occurrenceDate, this.description,
                this.amount, this.type, this.tag, this.categoryId, this.subCategoryId, this.wallet, this.ownerId);
    }
}