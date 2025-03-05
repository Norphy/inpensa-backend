package com.orphy.inpensa_backend.v1.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record User(

        @Max(value = 100, message = "User Id must be less than 100 characters")
        @NotEmpty(message = "User Id is mandatory")
        String id,
        @Max(value = 254, message = "Email must be less than 254 characters")
        @NotEmpty(message = "Email is mandatory")
        //^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
        //^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,
        @NotEmpty(message = "Creation date is mandatory")
        long dateCreated,

        @JsonDeserialize(using = Role.RoleDeserializer.class)
        @JsonSerialize(using = Role.RoleSerializer.class)
        @NotEmpty(message = "Role is mandatory")
        Role role) {

    public User copyWithId(String id) {
        return new User(id, this.email, this.dateCreated, this.role);
    }
}
