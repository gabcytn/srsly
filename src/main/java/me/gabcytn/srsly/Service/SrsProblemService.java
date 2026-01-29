package me.gabcytn.srsly.Service;

import static me.gabcytn.srsly.Model.Confidence.*;
import static me.gabcytn.srsly.Model.Difficulty.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.InitialSolutionDto;
import me.gabcytn.srsly.DTO.PaginatedSrsProblem;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.EarlyReviewException;
import me.gabcytn.srsly.Exception.SrsNotFound;
import me.gabcytn.srsly.Model.Confidence;
import me.gabcytn.srsly.Model.ProblemStatus;
import me.gabcytn.srsly.Repository.SrsProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SrsProblemService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SrsProblemService.class);
  private final SrsProblemRepository srsProblemRepository;
  private final UserService userService;

  public void saveInitial(InitialSolutionDto initialSolution, Problem problem, User user) {
    int reps = initialReps(initialSolution.repetitions());
    if (reps == 0) {
      this.save(SrsProblem.ofInitial(user, problem));
      return;
    }

    LocalDate dateNow = LocalDate.now();
    LocalDate lastReview = initialSolution.lastReviewedAt();
    double easeFactor = initialEaseFactor(initialSolution.confidence(), problem);
    int initialInterval = initialInterval(reps, easeFactor);
    int interval = (int) Math.min(initialInterval, dateDifference(lastReview, dateNow));
    LocalDate nextReview = lastReview.plusDays(interval);

    ProblemStatus status = reps <= 2 ? ProblemStatus.LEARNING : ProblemStatus.REVIEWING;

    this.save(
        new SrsProblem(status, easeFactor, reps, interval, lastReview, nextReview, user, problem));
  }

  public void saveSubsequent(int id, int grade) {
    Optional<SrsProblem> optionalSrsProblem = srsProblemRepository.findById(id);
    LocalDate dateNow = LocalDate.now();
    if (optionalSrsProblem.isEmpty()) {
      throw new SrsNotFound("Problem has not been solved. Come up with a solution first.");
    } else if (dateNow.isBefore(optionalSrsProblem.get().getNextAttemptAt())) {
      throw new EarlyReviewException();
    }

    SrsProblem srsProblem = optionalSrsProblem.get();
    if (grade < 3) {
      this.reviewFailed(srsProblem);
      return;
    }

    double updatedEaseFactor = calculateEaseFactor(srsProblem.getEaseFactor(), grade);
    if (dateNow.isAfter(srsProblem.getNextAttemptAt()) && grade == 5) {
      updatedEaseFactor += 0.05;
    }
    srsProblem.setEaseFactor(updatedEaseFactor);
    srsProblem.setRepetitions(srsProblem.getRepetitions() + 1);

    int repetitions = srsProblem.getRepetitions();
    int interval = srsProblem.getInterval();

    if (repetitions == 1) {
      interval = 1;
    } else if (repetitions == 2) {
      interval = 6;
    } else {
      double timingMultiplier = this.getTimingMultiplier(srsProblem, dateNow);
      interval = (int) Math.round(interval * updatedEaseFactor * timingMultiplier);
    }

    if (interval >= 60 && repetitions >= 4) {
      srsProblem.setStatus(ProblemStatus.MASTERED);
    } else if (repetitions > 2) {
      srsProblem.setStatus(ProblemStatus.REVIEWING);
    } else {
      srsProblem.setStatus(ProblemStatus.LEARNING);
    }

    srsProblem.setLastAttemptAt(dateNow);
    srsProblem.setNextAttemptAt(dateNow.plusDays(interval));
    srsProblem.setInterval(interval);

    this.save(srsProblem);
  }

  private void reviewFailed(SrsProblem srsProblem) {
    srsProblem.setRepetitions(0);
    srsProblem.setEaseFactor(Math.max(srsProblem.getEaseFactor() - 0.2, 1.3));
    srsProblem.setInterval(1);
    srsProblem.setStatus(ProblemStatus.LEARNING);
    srsProblem.setNextAttemptAt(LocalDate.now().plusDays(1));
    this.save(srsProblem);
  }

  public void save(SrsProblem srsProblem) {
    srsProblemRepository.save(srsProblem);
  }

  public PaginatedSrsProblem getTodayProblems(int page) {
    Pageable pageable = PageRequest.of(page, 10);
    Page<SrsProblem> paginatedSrsProblems =
        srsProblemRepository.findByUserAndNextAttemptAt(
            userService.getCurrentlyLoggedInUser(), LocalDate.now(), pageable);
    return new PaginatedSrsProblem(paginatedSrsProblems);
  }

  private double initialEaseFactor(Confidence confidence, Problem problem) {
    double easeFactor = 2.4;
    if (confidence == LOW) easeFactor -= 0.2;
    else if (confidence == HIGH) easeFactor += 0.2;

    if (problem.getDifficulty() == Easy) easeFactor += 0.1;
    else if (problem.getDifficulty() == Hard) easeFactor -= 0.1;

    return Math.min(easeFactor, 2.6);
  }

  private int initialReps(int repetitions) {
    int reps = repetitions;
    if (reps == 3) reps = 2;
    else if (reps >= 4) reps = 3;
    return reps;
  }

  private int initialInterval(int repetitions, double easeFactor) {
    if (repetitions == 0 || repetitions == 1) return 1;
    else if (repetitions == 2) return 6;
    else return (int) Math.round(6 * Math.pow(easeFactor, repetitions - 2));
  }

  private double calculateEaseFactor(double oldEaseFactor, int grade) {
    return Math.max(oldEaseFactor + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02)), 1.3);
  }

  private double getTimingMultiplier(SrsProblem problem, LocalDate dateNow) {
    double timingMultiplier = 1;
    if (dateNow.isAfter(problem.getNextAttemptAt())) {
      long delay = dateDifference(problem.getNextAttemptAt(), dateNow.plusDays(1));
      double ratio = (double) delay / problem.getInterval();
      timingMultiplier += (ratio * 0.4);
    }
    return timingMultiplier;
  }

  private long dateDifference(LocalDate from, LocalDate to) {
    return ChronoUnit.DAYS.between(from, to);
  }
}
