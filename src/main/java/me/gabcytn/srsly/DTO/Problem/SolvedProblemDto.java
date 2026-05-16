package me.gabcytn.srsly.DTO.Problem;

import java.time.LocalDate;

public record SolvedProblemDto(ProblemSummaryDto problem, ReviewDetail reviewDetails, LocalDate solvedAt) {}
