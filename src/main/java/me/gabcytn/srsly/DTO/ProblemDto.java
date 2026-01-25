package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Model.Difficulty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProblemDto
{
  @JsonView(Views.Summary.class)
  private int questionFrontendId;

  @JsonView(Views.Summary.class)
  private String title;

  @JsonView(Views.Detailed.class)
  private String content;

  @JsonView(Views.Summary.class)
  private Difficulty difficulty;

  @JsonView(Views.Summary.class)
  private String url;

  public Problem toProblemEntity() {
    return new Problem(questionFrontendId, title, content, difficulty, url);
  }
}
