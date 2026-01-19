package me.gabcytn.srsly.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Model.Difficulty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LeetCodeApiPied {
  private int questionFrontendId;
  private String title;
  private String content;
  private Difficulty difficulty;
  private String url;

  public Problem toProblemEntity() {
    return new Problem(questionFrontendId, title, content, difficulty, url);
  }
}
