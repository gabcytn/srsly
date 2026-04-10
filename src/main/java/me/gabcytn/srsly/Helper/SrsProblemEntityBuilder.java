package me.gabcytn.srsly.Helper;

import java.time.LocalDate;
import me.gabcytn.srsly.DTO.ProblemStatus;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Entity.User;

public class SrsProblemEntityBuilder {

  private ProblemStatus status;

  private Double easeFactor;

  private Integer repetitions;

  private Integer interval;

  private LocalDate lastAttemptAt;

  private LocalDate nextAttemptAt;

  private User user;

  private Problem problem;

  public SrsProblemEntityBuilder status(ProblemStatus status) {
    this.status = status;
    return this;
  }

  public SrsProblemEntityBuilder easeFactor(Double easeFactor) {
    this.easeFactor = easeFactor;
    return this;
  }

  public SrsProblemEntityBuilder repetitions(Integer repetitions) {
    this.repetitions = repetitions;
    return this;
  }

  public SrsProblemEntityBuilder interval(Integer interval) {
    this.interval = interval;
    return this;
  }

  public SrsProblemEntityBuilder lastAttemptAt(LocalDate lastAttemptAt) {
    this.lastAttemptAt = lastAttemptAt;
    return this;
  }

  public SrsProblemEntityBuilder nextAttemptAt(LocalDate nextAttemptAt) {
    this.nextAttemptAt = nextAttemptAt;
    return this;
  }

  public SrsProblemEntityBuilder user(User user) {
    this.user = user;
    return this;
  }

  public SrsProblemEntityBuilder problem(Problem problem) {
    this.problem = problem;
    return this;
  }

  public SrsProblem build() {
    SrsProblem entity = new SrsProblem();
    entity.setStatus(status);
    entity.setEaseFactor(easeFactor);
    entity.setRepetitions(repetitions);
    entity.setInterval(interval);
    entity.setLastAttemptAt(lastAttemptAt);
    entity.setNextAttemptAt(nextAttemptAt);
    entity.setUser(user);
    entity.setProblem(problem);
    return entity;
  }
}
