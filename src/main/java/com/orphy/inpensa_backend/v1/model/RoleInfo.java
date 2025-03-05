package com.orphy.inpensa_backend.v1.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.UUID;

public record RoleInfo(UUID id,

                       @JsonDeserialize(using = Role.RoleDeserializer.class)
                       @JsonSerialize(using = Role.RoleSerializer.class)
                       Role role,
                       long dateCreated,
                       String createdBy) {
    public RoleInfo copyWithId(UUID id) {
        return new RoleInfo(id, this.role, this.dateCreated, this.createdBy);
    }
}
