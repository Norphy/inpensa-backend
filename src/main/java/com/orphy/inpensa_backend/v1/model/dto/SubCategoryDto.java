package com.orphy.inpensa_backend.v1.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;

import java.util.UUID;

public record SubCategoryDto(
                            @NotEmpty(message = "SubCategory is mandatory")
                            @Max(value = 50, message = "SubCategory must be less than 50 characters")
                            String subCategory,
                            @NotEmpty(message = "Category ID is mandatory")
                            UUID categoryId) {
}
