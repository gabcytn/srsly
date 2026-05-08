package me.gabcytn.srsly.DTO;

import java.util.HashSet;
import java.util.List;
import lombok.*;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Tag;

@NoArgsConstructor
@Setter
@Getter
public class LeetCodeProblemApiResponse {
  @NonNull private Integer questionFrontendId;

  @NonNull private String title;

  @NonNull private String content;

  @NonNull private Difficulty difficulty;

  @NonNull private List<TagDto> topicTags;

  @NonNull private String url;

  public void setDifficulty(String difficulty) {
    this.difficulty = Difficulty.valueOf(difficulty.toUpperCase());
  }

  public Problem toProblemEntity(List<Tag> tags) {
    return new Problem(questionFrontendId, title, content, difficulty, url, new HashSet<>(tags));
  }
}
