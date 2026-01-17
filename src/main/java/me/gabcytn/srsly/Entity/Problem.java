package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import java.util.Set;
import me.gabcytn.srsly.Model.Difficulty;

@Entity
@Table(name = "problems")
public class Problem {
  @Id private int id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
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
      joinColumns = @JoinColumn(name = "problem_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags;
}
