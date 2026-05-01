package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
    name = "solved_problems",
    indexes = {
      @Index(name = "user_solved_problems_idx", columnList = "user_id"),
      @Index(name = "problem_solved_problems_idx", columnList = "problem_id")
    })
public class SolvedProblem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "problem_id", nullable = false)
  private Problem problem;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public SolvedProblem() {}

  public SolvedProblem(Problem problem, User user) {
    this.problem = problem;
    this.user = user;
  }
}
