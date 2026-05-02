package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;
import me.gabcytn.srsly.DTO.Problem.ReviewDetail;
import me.gabcytn.srsly.DTO.Problem.SolvedProblemDto;

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

  @OneToOne(mappedBy = "solvedProblem", fetch = FetchType.LAZY)
  private ReviewProblem reviewProblem;

  public SolvedProblem() {}

  public SolvedProblem(Problem problem, User user) {
    this.problem = problem;
    this.user = user;
  }

  public SolvedProblemDto toDto() {
    ProblemSummaryDto problemSummary = this.problem.summarize();
    ReviewDetail reviewDetail = null;
    if (this.reviewProblem != null) {
      reviewDetail =
          new ReviewDetail(
              reviewProblem.getId(),
              reviewProblem.getLastAttemptAt(),
              reviewProblem.getNextAttemptAt(),
              reviewProblem.getStatus());
    }

    return new SolvedProblemDto(problemSummary, reviewDetail);
  }
}
