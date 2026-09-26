package me.gabcytn.srsly.Review.DTO;

import java.time.LocalDate;
import lombok.*;
import me.gabcytn.srsly.Problem.DTO.ProblemStatus;
import me.gabcytn.srsly.Problem.DTO.ProblemSummaryDto;

@Builder
@Data
public class ReviewProblemDto {
  private long id;

  private LocalDate lastAttemptAt;

  private LocalDate nextAttemptAt;

  private ProblemStatus status;

  private ProblemSummaryDto problem;
}
