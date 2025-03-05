package com.orphy.inpensa_backend.v1.model.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;

public record WalletDto(
                        @NotEmpty(message = "Wallet Name is mandatory")
                        @Max(value = 50, message = "Wallet Name must be less than 50 characters")
                        String name,
                        //Amount will default to 0 in DB when not given
                        long amount) {
}
