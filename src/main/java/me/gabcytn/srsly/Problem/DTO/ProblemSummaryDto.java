package me.gabcytn.srsly.Problem.DTO;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProblemSummaryDto {
  private Integer questionFrontendId;

  private String title;

  private Difficulty difficulty;

  private List<TagDto> topicTags;

  private String url;
}
