package me.gabcytn.srsly.DTO;

import jakarta.validation.constraints.NotNull;
import me.gabcytn.srsly.DTO.Annotation.IsGradeValid;

public record ReviewedProblem(@NotNull(message = "Grade is required.") @IsGradeValid Integer grade) {}
