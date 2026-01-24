package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.DTO.SrsProblemDto;
import me.gabcytn.srsly.Model.ProblemStatus;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "srs_problems")
public class SrsProblem {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProblemStatus status = ProblemStatus.NEW;

  @Column(nullable = false, columnDefinition = "DECIMAL(1, 2)")
  private double easeFactor = 2.5;

  @Column(nullable = false)
  private int repetitions = 1;

  @Column(nullable = false)
  private int previousInterval = 1;

  @Column(nullable = false)
  private LocalDate lastAttemptAt;

  @Column(nullable = false)
  private LocalDate nextAttemptAt;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "problem_id", nullable = false)
  private Problem problem;

  public SrsProblemDto toDto() {
    SrsProblemDto dto = new SrsProblemDto();
    dto.setProblem(problem.toApiPied());
    dto.setStatus(status);
    dto.setRepetitions(repetitions);
    dto.setLastAttemptAt(lastAttemptAt);
    return dto;
  }
}
