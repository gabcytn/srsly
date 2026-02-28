package me.gabcytn.srsly.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditSolution(
    @NotNull(message = "Code is required.") @NotBlank(message = "Code must not be blank.")
        String code,
    @NotNull(message = "Note is required.") String note) {}
