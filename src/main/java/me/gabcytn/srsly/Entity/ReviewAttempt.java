package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "review_attempts",
    indexes = {
      @Index(name = "idx_attempt_date", columnList = "attempted_at"),
      @Index(name = "idx_attempt_solved_problem", columnList = "solved_problem_id")
    })
public class ReviewAttempt
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private Double easeFactor;

  private Integer grade;

  @Column(nullable = false)
  private LocalDate attemptedAt;

  @ManyToOne
  @JoinColumn(name = "solved_problem_id", nullable = false)
  private SolvedProblem solvedProblem;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public static ReviewAttempt fromSolvedProblem(ReviewProblem problem) {
    return new ReviewAttempt(
        problem.getEaseFactor(),
        problem.getLastAttemptAt(),
        problem.getSolvedProblem());
  }

  public static ReviewAttempt fromSolvedProblem(ReviewProblem problem, int grade) {
    return new ReviewAttempt(
        problem.getEaseFactor(),
        grade,
        problem.getLastAttemptAt(),
        problem.getSolvedProblem());
  }

  private ReviewAttempt(
      Double easeFactor, Integer grade, LocalDate attemptedAt, SolvedProblem solvedProblem) {
    this.easeFactor = easeFactor;
    this.grade = grade;
    this.attemptedAt = attemptedAt;
    this.solvedProblem = solvedProblem;
  }

  private ReviewAttempt(double easeFactor, LocalDate attemptedAt, SolvedProblem solvedProblem) {
    this.easeFactor = easeFactor;
    this.attemptedAt = attemptedAt;
    this.solvedProblem = solvedProblem;
  }
}
