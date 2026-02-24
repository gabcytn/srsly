package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.DTO.ProblemDto;
import me.gabcytn.srsly.DTO.TagDto;
import me.gabcytn.srsly.Model.Difficulty;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "problems",
    indexes = {
      @Index(name = "idx_problem_title", columnList = "title"),
      @Index(name = "idx_problem_difficulty", columnList = "difficulty")
    })
public class Problem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Column(nullable = false, unique = true)
  private int frontendId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String question;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Difficulty difficulty;

  @Column(nullable = false)
  private String url;

  @OneToMany(mappedBy = "problem")
  private Set<SrsProblem> srsProblem;

  @ManyToMany
  @JoinTable(
      name = "problem_tags",
      joinColumns = @JoinColumn(name = "problem_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false))
  private Set<Tag> tags;

  public Problem(
      int id, String title, String question, Difficulty difficulty, Set<Tag> tags, String url) {
    this.frontendId = id;
    this.title = title;
    this.question = question;
    this.difficulty = difficulty;
    this.tags = tags;
    this.url = url;
  }

  public ProblemDto toApiPied() {
    return new ProblemDto(frontendId, title, question, difficulty, getTagNames(), url);
  }

  private List<TagDto> getTagNames() {
    return tags.stream().map(i -> new TagDto(i.getName())).collect(Collectors.toList());
  }
}
