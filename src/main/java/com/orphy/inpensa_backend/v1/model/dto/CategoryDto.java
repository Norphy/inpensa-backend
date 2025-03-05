package com.orphy.inpensa_backend.v1.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;

public record CategoryDto(@NotEmpty(message = "Category is mandatory")
                          @Max(value = 50, message = "Category must be less than 50 characters")
                          String category) {
}
