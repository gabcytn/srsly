package me.gabcytn.srsly.Problem.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemSearchFilter {
  private int page;
  private String difficulty;
  private String title;
}
