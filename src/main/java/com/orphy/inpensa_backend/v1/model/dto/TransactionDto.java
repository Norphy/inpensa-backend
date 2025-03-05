package com.orphy.inpensa_backend.v1.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.orphy.inpensa_backend.v1.model.TransactionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

public record TransactionDto(
                            @NotEmpty(message = "Occurrence Date is mandatory")
                            long occurrenceDate,
                            @NotEmpty(message = "Description is mandatory")
                            @Max(value = 500, message = "Description must be less than 500 characters")
                            String description,
                            @NotEmpty(message = "Amount is mandatory")
                            int amount,
                            @NotEmpty(message = "Transaction Type is mandatory")
                            @JsonDeserialize(using = TransactionType.TransactionTypeDeserializer.class)
                            TransactionType type,
                            @NotEmpty(message = "Tag is mandatory")
                            @Max(value = 40, message = "Tag must be less than 40 characters")
                            String tag,
                            @NotEmpty(message = "Category ID is mandatory")
                            UUID categoryId,
                            @NotEmpty(message = "SubCategory ID is mandatory")
                            UUID subCategoryId,
                            @NotEmpty(message = "Wallet ID is mandatory")
                            UUID walletId
                             ) {
}
