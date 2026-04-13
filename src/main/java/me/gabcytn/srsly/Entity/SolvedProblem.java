package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import me.gabcytn.srsly.DTO.ProblemStatus;
import me.gabcytn.srsly.DTO.SolvedProblemDto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "solved_problems",
    indexes = {
      @Index(name = "solved_idx_problem", columnList = "problem_id"),
      @Index(name = "solved_idx_user", columnList = "user_id")
    })
public class SolvedProblem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @NonNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProblemStatus status;

  @NonNull
  @Column(nullable = false)
  private Double easeFactor;

  @NonNull
  @Column(nullable = false)
  private Integer repetitions;

  @NonNull
  @Column(nullable = false)
  private Integer interval;

  @NonNull
  @Column(nullable = false)
  private LocalDate lastAttemptAt;

  private LocalDate nextAttemptAt;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NonNull
  @ManyToOne
  @JoinColumn(name = "problem_id", nullable = false)
  private Problem problem;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp private LocalDateTime updatedAt;

  public static SolvedProblem ofInitial(Problem problem, User user) {
    double easeFactor =
        switch (problem.getDifficulty()) {
          case Easy -> 2.6;
          case Medium -> 2.4;
          case Hard -> 2.2;
        };

    LocalDate dateNow = LocalDate.now();
    return SolvedProblem.builder()
        .status(ProblemStatus.NEW)
        .easeFactor(easeFactor)
        .repetitions(0)
        .interval(1)
        .lastAttemptAt(dateNow)
        .nextAttemptAt(dateNow.plusDays(1))
        .user(user)
        .problem(problem)
        .build();
  }

  public SolvedProblemDto toDto() {
    return SolvedProblemDto.builder()
        .id(id)
        .repetitions(repetitions)
        .lastAttemptAt(lastAttemptAt)
        .nextAttemptAt(nextAttemptAt)
        .status(status)
        .problem(problem.summarize())
        .build();
  }
}
