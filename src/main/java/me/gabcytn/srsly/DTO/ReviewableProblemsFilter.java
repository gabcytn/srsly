package me.gabcytn.srsly.DTO;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ReviewableProblemsFilter {
  private int page;
  private String difficulty;
  private String title;
}
