package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "attempts",
    indexes = {
      @Index(name = "idx_attempt_date", columnList = "attempted_at"),
      @Index(name = "idx_attempt_user", columnList = "user_id")
    })
public class Attempt {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NonNull
  @Column(nullable = false)
  private Double easeFactor;

  private Integer grade;

  @NonNull
  @Column(nullable = false)
  private LocalDate attemptedAt;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "problem_id", nullable = false)
  private Problem problem;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public Attempt(
      Double easeFactor, Integer grade, LocalDate attemptedAt, Problem problem, User user) {
    this.easeFactor = easeFactor;
    this.grade = grade;
    this.attemptedAt = attemptedAt;
    this.problem = problem;
    this.user = user;
  }

  public static Attempt fromSolvedProblem(SolvedProblem problem) {
    return new Attempt(
        problem.getEaseFactor(),
        problem.getLastAttemptAt(),
        problem.getProblem(),
        problem.getUser());
  }

  public static Attempt fromSolvedProblem(SolvedProblem problem, int grade) {
    return new Attempt(
        problem.getEaseFactor(),
        grade,
        problem.getLastAttemptAt(),
        problem.getProblem(),
        problem.getUser());
  }
}
