package me.gabcytn.srsly.Review.DTO;

import lombok.Builder;
import lombok.Data;
import me.gabcytn.srsly.Problem.Entity.SolvedProblem;

@Data
@Builder
public class ProblemSubmissionWithHistory {
  private InitialReviewRequest initialReview;
  private SolvedProblem solvedProblem;
  private Integer repetitions;
}
