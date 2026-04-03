package me.gabcytn.srsly.DTO.Problem;

import java.util.HashSet;
import java.util.List;
import lombok.Data;
import me.gabcytn.srsly.DTO.Difficulty;
import me.gabcytn.srsly.DTO.TagDto;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Tag;

@Data
public class ProblemDetailDto {
  private Integer questionFrontendId;

  private String title;

  private Difficulty difficulty;

  private List<TagDto> topicTags;

  private String url;

  /* Additional Info */

  private String content;

  private Boolean isSolved;

  private ReviewDetail reviewDetail;

  public Problem toProblemEntity(List<Tag> tags) {
    return new Problem(questionFrontendId, title, content, difficulty, url, new HashSet<>(tags));
  }

  public ProblemDetailDto(ProblemSummaryDto summary) {
    questionFrontendId = summary.getQuestionFrontendId();
    title = summary.getTitle();
    difficulty = summary.getDifficulty();
    topicTags = summary.getTopicTags();
    url = summary.getUrl();
  }
}
