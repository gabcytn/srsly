package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.Model.ProblemStatus;

import java.sql.Date;

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

  @Column(nullable = false)
  private int easeFactor = 2;

  @Column(nullable = false)
  private int repetitions = 0;

  @Column(nullable = false)
  private Date lastAttemptAt;

  @Column(nullable = false)
  private Date nextAttemptAt;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "problem_id", nullable = false)
  private Problem problem;
}
