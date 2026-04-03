package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import me.gabcytn.srsly.DTO.Annotation.ValidInitialSolution;

@ValidInitialSolution
public record InitialSolutionDto(
    @NotNull(message = "Repetitions is required.") Integer repetitions,
    LocalDate lastReviewedAt,
    Confidence confidence,
    @JsonProperty("solution") @Valid SolutionDto solutionDto) {}
