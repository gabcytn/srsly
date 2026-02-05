package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.HashSet;
import java.util.List;
import lombok.*;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Tag;
import me.gabcytn.srsly.Model.Difficulty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProblemDto {
  @JsonView(Views.Summary.class)
  private int questionFrontendId;

  @JsonView(Views.Summary.class)
  private String title;

  @JsonView(Views.Detailed.class)
  private String content;

  @JsonView(Views.Summary.class)
  private Difficulty difficulty;

  @JsonView(Views.Summary.class)
  private List<TagDto> topicTags;

  @JsonView(Views.Summary.class)
  private String url;

  public Problem toProblemEntity(List<Tag> tags) {
    return new Problem(questionFrontendId, title, content, difficulty, new HashSet<>(tags), url);
  }
}
