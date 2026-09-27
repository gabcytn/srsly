package me.gabcytn.srsly.Review.DTO;

import jakarta.validation.constraints.NotNull;
import me.gabcytn.srsly.Review.DTO.Annotation.IsGradeValid;

public record ReviewedProblem(
    @NotNull(message = "Grade is required.") @IsGradeValid Integer grade) {}
