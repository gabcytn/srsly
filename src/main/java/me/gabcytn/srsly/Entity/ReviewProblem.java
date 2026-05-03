package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import me.gabcytn.srsly.DTO.ProblemStatus;
import me.gabcytn.srsly.DTO.ReviewProblemDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "review_problems",
    indexes = {
      @Index(name = "solved_problem_idx", columnList = "solved_problem_id"),
      @Index(name = "next_attempt_at_idx", columnList = "next_attempt_at")
    })
public class ReviewProblem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProblemStatus status;

  @Column(nullable = false)
  private Double easeFactor;

  @Column(nullable = false)
  private Integer repetitions;

  @Column(nullable = false)
  private Integer interval;

  @Column(nullable = false)
  private LocalDate lastAttemptAt;

  @Column(nullable = false)
  private LocalDate nextAttemptAt;

  @OneToOne
  @JoinColumn(name = "solved_problem_id", nullable = false, referencedColumnName = "id")
  private SolvedProblem solvedProblem;

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public static ReviewProblem ofReviewableInitial(SolvedProblem solvedProblem) {
    Problem problem = solvedProblem.getProblem();
    double easeFactor =
        switch (problem.getDifficulty()) {
          case EASY -> 2.6;
          case MEDIUM -> 2.4;
          case HARD -> 2.2;
        };

    LocalDate dateNow = LocalDate.now();
    return ReviewProblem.builder()
        .status(ProblemStatus.NEW)
        .easeFactor(easeFactor)
        .repetitions(0)
        .interval(1)
        .lastAttemptAt(dateNow)
        .nextAttemptAt(dateNow.plusDays(1))
        .solvedProblem(solvedProblem)
        .build();
  }

  public ReviewProblemDto toDto() {
    return ReviewProblemDto.builder()
        .id(id)
        .lastAttemptAt(lastAttemptAt)
        .nextAttemptAt(nextAttemptAt)
        .status(status)
        .problem(solvedProblem.getProblem().summarize())
        .build();
  }
}
