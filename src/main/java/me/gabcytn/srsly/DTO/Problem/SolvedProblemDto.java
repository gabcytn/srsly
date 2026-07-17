package me.gabcytn.srsly.DTO.Problem;

import java.time.LocalDate;
import lombok.Data;

@Data
public class SolvedProblemDto {
  private ProblemSummaryDto problem;
  private ReviewDetail reviewDetails;
  private LocalDate solvedAt;

  private SolvedProblemDto(
      ProblemSummaryDto problem, ReviewDetail reviewDetails, LocalDate solvedAt) {
    this.problem = problem;
    this.reviewDetails = reviewDetails;
    this.solvedAt = solvedAt;
  }

  public static SolvedProblemDto ofNonReviewable(ProblemSummaryDto problem, LocalDate solvedAt) {
    return new SolvedProblemDto(problem, null, solvedAt);
  }

  public static SolvedProblemDto ofReviewable(
      ProblemSummaryDto problem, ReviewDetail reviewDetail, LocalDate solvedAt) {
    return new SolvedProblemDto(problem, reviewDetail, solvedAt);
  }
}
