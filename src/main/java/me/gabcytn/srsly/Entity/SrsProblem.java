package me.gabcytn.srsly.Entity;

import static me.gabcytn.srsly.DTO.Difficulty.Easy;
import static me.gabcytn.srsly.DTO.Difficulty.Medium;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import me.gabcytn.srsly.DTO.ProblemStatus;
import me.gabcytn.srsly.DTO.SrsProblemDto;
import me.gabcytn.srsly.Helper.SrsProblemEntityBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Entity
@Table(
    name = "srs_problems",
    indexes = {
      @Index(name = "srs_idx_problem", columnList = "problem_id"),
      @Index(name = "srs_idx_user", columnList = "user_id")
    })
public class SrsProblem {
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

  @NonNull
  @Column(nullable = false)
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

  public static SrsProblem ofInitial(Problem problem, User user) {
    double easeFactor;
    if (problem.getDifficulty().equals(Easy)) easeFactor = 2.6;
    else if (problem.getDifficulty().equals(Medium)) easeFactor = 2.4;
    else easeFactor = 2.2;

    LocalDate dateNow = LocalDate.now();
    return new SrsProblemEntityBuilder()
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

  public SrsProblemDto toDto() {
    SrsProblemDto dto = new SrsProblemDto();
    dto.setId(id);
    dto.setProblem(problem.summarize());
    dto.setStatus(status);
    dto.setRepetitions(repetitions);
    dto.setLastAttemptAt(lastAttemptAt);
    dto.setNextAttemptAt(nextAttemptAt);
    return dto;
  }
}
