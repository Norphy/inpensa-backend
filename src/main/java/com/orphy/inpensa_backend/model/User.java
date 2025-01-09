package com.orphy.inpensa_backend.model;

import java.util.UUID;

public record User(String id, String email, Role role) {
}
