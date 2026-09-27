package me.gabcytn.srsly.Review.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import me.gabcytn.srsly.Problem.DTO.Confidence;
import me.gabcytn.srsly.Review.DTO.Annotation.ValidInitialReview;
import me.gabcytn.srsly.Solution.DTO.SolutionDto;

@ValidInitialReview
public record InitialReviewRequest(
    @NotNull(message = "Repetitions is required.") Integer repetitions,
    LocalDate lastReviewedAt,
    Confidence confidence,
    @Valid SolutionDto solution) {}
