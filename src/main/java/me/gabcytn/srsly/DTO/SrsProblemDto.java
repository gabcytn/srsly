package me.gabcytn.srsly.DTO;

import java.time.LocalDate;
import lombok.*;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SrsProblemDto {
  private int id;

  private int repetitions;

  private LocalDate lastAttemptAt;

  private LocalDate nextAttemptAt;

  private ProblemStatus status;

  private ProblemSummaryDto problem;

  public static class Builder {
    private int id;

    private int repetitions;

    private LocalDate lastAttemptAt;

    private LocalDate nextAttemptAt;

    private ProblemStatus status;

    private ProblemSummaryDto problem;

    public Builder id(int id) {
      this.id = id;
      return this;
    }

    public Builder repetitions(int repetitions) {
      this.repetitions = repetitions;
      return this;
    }

    public Builder lastAttemptAt(LocalDate date) {
      this.lastAttemptAt = date;
      return this;
    }

    public Builder nextAttemptAt(LocalDate date) {
      this.nextAttemptAt = date;
      return this;
    }

    public Builder problemStatus(ProblemStatus status) {
      this.status = status;
      return this;
    }

    public Builder problemSummary(ProblemSummaryDto problem) {
      this.problem = problem;
      return this;
    }

    public SrsProblemDto build() {
      return new SrsProblemDto(id, repetitions, lastAttemptAt, nextAttemptAt, status, problem);
    }
  }
}
