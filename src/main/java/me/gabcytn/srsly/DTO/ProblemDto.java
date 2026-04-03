package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import lombok.*;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Tag;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProblemDto {
  @NonNull
  @JsonView(Views.Summary.class)
  private Integer questionFrontendId;

  @NonNull
  @JsonView(Views.Summary.class)
  private String title;

  @NonNull
  @JsonView(Views.Detailed.class)
  private String content;

  @JsonView(Views.Detailed.class)
  private Boolean isSolved;

  @JsonView(Views.Detailed.class)
  private int srsId;

  @JsonView(Views.Detailed.class)
  private LocalDate nextAttemptAt;

  @NonNull
  @JsonView(Views.Summary.class)
  private Difficulty difficulty;

  @NonNull
  @JsonView(Views.Summary.class)
  private List<TagDto> topicTags;

  @NonNull
  @JsonView(Views.Summary.class)
  private String url;

  public Problem toProblemEntity(List<Tag> tags) {
    return new Problem(questionFrontendId, title, content, difficulty, url, new HashSet<>(tags));
  }
}
