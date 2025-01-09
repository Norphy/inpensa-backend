package com.orphy.inpensa_backend.model.dto;

import com.orphy.inpensa_backend.model.TransactionType;
import jakarta.annotation.Nonnull;

import java.util.UUID;

public record TransactionDto(long occurrenceDate,
                             String description,
                             int amount,
                             TransactionType type,
                             String tag,
                             UUID categoryId,
                             UUID subCategoryId,
                             UUID wallet
                             ) {
}
