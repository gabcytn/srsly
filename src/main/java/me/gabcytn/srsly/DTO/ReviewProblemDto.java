package me.gabcytn.srsly.DTO;

import java.time.LocalDate;
import lombok.*;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;

@Builder
@Data
public class ReviewProblemDto {
  private long id;

  private LocalDate lastAttemptAt;

  private LocalDate nextAttemptAt;

  private ProblemStatus status;

  private ProblemSummaryDto problem;
}
