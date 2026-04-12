package me.gabcytn.srsly.DTO.Review;

import lombok.Builder;
import lombok.Data;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.User;

@Data
@Builder
public class ProblemSubmissionWithHistory {
  private InitialReviewRequest initialReview;
  private Problem problem;
  private User user;
  private Integer repetitions;
}
