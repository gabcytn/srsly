package me.gabcytn.srsly.DTO.Review;

import lombok.Builder;
import lombok.Data;
import me.gabcytn.srsly.Entity.SolvedProblem;

@Data
@Builder
public class ProblemSubmissionWithHistory {
  private InitialReviewRequest initialReview;
  private SolvedProblem solvedProblem;
  private Integer repetitions;
}
