package com.orphy.inpensa_backend.model.dto;

import java.util.UUID;

public record WalletDto(String name,
                        long dateCreated,
                        long amount,
                        String ownerId) {
}
