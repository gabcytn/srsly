package me.gabcytn.srsly.DTO;

import java.time.LocalDate;
import lombok.*;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;

@Builder
@Data
public class SrsProblemDto {
  private int id;

  private int repetitions;

  private LocalDate lastAttemptAt;

  private LocalDate nextAttemptAt;

  private ProblemStatus status;

  private ProblemSummaryDto problem;
}
