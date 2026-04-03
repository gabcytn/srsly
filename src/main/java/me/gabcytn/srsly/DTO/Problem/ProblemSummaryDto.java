package me.gabcytn.srsly.DTO.Problem;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.gabcytn.srsly.DTO.Difficulty;
import me.gabcytn.srsly.DTO.TagDto;

@AllArgsConstructor
@Data
public class ProblemSummaryDto {
  private Integer questionFrontendId;

  private String title;

  private Difficulty difficulty;

  private List<TagDto> topicTags;

  private String url;
}
