package me.gabcytn.srsly.DTO.Review;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitialProblemReview {
  private InitialReviewRequest initialReviewRequest;
  private Integer problemFrontendId;
  private Boolean isReviewable;
}
