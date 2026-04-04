package me.gabcytn.srsly.DTO;

import java.util.HashSet;
import java.util.List;
import lombok.*;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Tag;

@AllArgsConstructor
@Data
public class LeetCodeProblemApiResponse {
  @NonNull private Integer questionFrontendId;

  @NonNull private String title;

  @NonNull private String content;

  @NonNull private Difficulty difficulty;

  @NonNull private List<TagDto> topicTags;

  @NonNull private String url;

  public Problem toProblemEntity(List<Tag> tags) {
    return new Problem(questionFrontendId, title, content, difficulty, url, new HashSet<>(tags));
  }
}
