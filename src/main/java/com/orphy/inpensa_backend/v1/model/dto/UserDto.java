package com.orphy.inpensa_backend.v1.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

//TODO is this needed?
public record UserDto(
        @Max(value = 100, message = "User Id must be less than 100 characters")
        @NotEmpty(message = "User Id is mandatory")
        String userId,

        @Max(value = 254, message = "Email must be less than 254 characters")
        @NotEmpty(message = "Email is mandatory")
        //^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
        //^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email) { }
