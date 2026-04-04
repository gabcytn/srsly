package me.gabcytn.srsly.DTO;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import lombok.*;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Tag;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProblemDto {
  @NonNull private Integer questionFrontendId;

  @NonNull private String title;

  @NonNull private String content;

  private Boolean isSolved;

  private int srsId;

  private LocalDate nextAttemptAt;

  @NonNull private Difficulty difficulty;

  @NonNull private List<TagDto> topicTags;

  @NonNull private String url;

  public Problem toProblemEntity(List<Tag> tags) {
    return new Problem(questionFrontendId, title, content, difficulty, url, new HashSet<>(tags));
  }
}
