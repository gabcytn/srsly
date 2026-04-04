package me.gabcytn.srsly.DTO.Review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import me.gabcytn.srsly.DTO.Confidence;
import me.gabcytn.srsly.DTO.SolutionDto;

public record InitialReviewRequest(
    @NotNull(message = "Repetitions is required.") Integer repetitions,
    LocalDate lastReviewedAt,
    Confidence confidence,
    @Valid SolutionDto solution) {}
