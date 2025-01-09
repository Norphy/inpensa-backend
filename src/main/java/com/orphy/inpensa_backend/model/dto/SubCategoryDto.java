package com.orphy.inpensa_backend.model.dto;

import java.util.UUID;

public record SubCategoryDto(String subCategory, UUID categoryId, String userId) {
}
