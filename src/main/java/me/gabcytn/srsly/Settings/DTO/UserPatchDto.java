package me.gabcytn.srsly.Settings.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPatchDto(
    @NotNull(message = "Name is required.") @NotBlank(message = "Name must not be blank.")
        String name) {}
