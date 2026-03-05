package me.gabcytn.srsly.Service;

import static me.gabcytn.srsly.Model.Confidence.*;
import static me.gabcytn.srsly.Model.Difficulty.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.InitialSolutionDto;
import me.gabcytn.srsly.DTO.PaginatedSrsProblem;
import me.gabcytn.srsly.Entity.Attempt;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.EarlyReviewException;
import me.gabcytn.srsly.Exception.GenericNotFoundException;
import me.gabcytn.srsly.Exception.SrsNotFound;
import me.gabcytn.srsly.Model.Confidence;
import me.gabcytn.srsly.Model.Difficulty;
import me.gabcytn.srsly.Model.ProblemStatus;
import me.gabcytn.srsly.Repository.SrsProblemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SrsProblemService {
  private static final BigDecimal ZERO_POINT_TWO = BigDecimal.valueOf(0.2);
  private static final BigDecimal ZERO_POINT_ONE = BigDecimal.valueOf(0.1);
  private static final BigDecimal ONE_POINT_THREE = BigDecimal.valueOf(1.3);
  private static final BigDecimal ZERO_POINT_ZERO_EIGHT = BigDecimal.valueOf(0.08);
  private static final BigDecimal ZERO_POINT_ZERO_TWO = BigDecimal.valueOf(0.02);
  private static final BigDecimal FIVE = BigDecimal.valueOf(5);

  private final SrsProblemRepository srsProblemRepository;
  private final UserService userService;
  private final AttemptService attemptService;

  public void saveInitial(InitialSolutionDto initialSolution, Problem problem, User user) {
    int reps = initialReps(initialSolution.repetitions());
    if (reps == 0) {
      SrsProblem srsProblem = this.save(SrsProblem.ofInitial(user, problem));
      attemptService.save(Attempt.fromSrsProblem(srsProblem));
      return;
    }

    LocalDate dateNow = LocalDate.now();
    LocalDate lastReview = initialSolution.lastReviewedAt();
    double easeFactor = initialEaseFactor(initialSolution.confidence(), problem);
    int initialInterval = initialInterval(reps, easeFactor);
    long dateDifference = dateDifference(lastReview, dateNow);
    if (dateDifference == 0) dateDifference++;
    int interval = (int) Math.min(initialInterval, dateDifference);
    LocalDate nextReview = lastReview.plusDays(interval);

    ProblemStatus status = reps <= 2 ? ProblemStatus.LEARNING : ProblemStatus.REVIEWING;

    SrsProblem srsProblem =
        this.save(
            new SrsProblem(
                status, easeFactor, reps, interval, lastReview, nextReview, user, problem));
    attemptService.save(Attempt.fromSrsProblem(srsProblem));
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
      this.reviewFailed(srsProblem, grade);
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

    SrsProblem savedProblem = this.save(srsProblem);
    attemptService.save(Attempt.fromSrsProblem(savedProblem, grade));
  }

  private void reviewFailed(SrsProblem srsProblem, int grade) {
    BigDecimal easeFactor = BigDecimal.valueOf(srsProblem.getEaseFactor());
    BigDecimal failedEaseFactor = easeFactor.subtract(ZERO_POINT_TWO);

    LocalDate now = LocalDate.now();
    srsProblem.setEaseFactor(failedEaseFactor.max(ONE_POINT_THREE).doubleValue());
    srsProblem.setRepetitions(0);
    srsProblem.setInterval(1);
    srsProblem.setStatus(ProblemStatus.LEARNING);
    srsProblem.setLastAttemptAt(now);
    srsProblem.setNextAttemptAt(now.plusDays(1));
    SrsProblem savedProblem = this.save(srsProblem);
    attemptService.save(Attempt.fromSrsProblem(savedProblem, grade));
  }

  public SrsProblem save(SrsProblem srsProblem) {
    return srsProblemRepository.save(srsProblem);
  }

  public PaginatedSrsProblem getTodayProblems(int page, String difficulty, String titleSearch) {
    Pageable pageable = PageRequest.of(page, 5, Sort.by("nextAttemptAt"));
    User currentUser = userService.getCurrentlyLoggedInUser();
    LocalDate dateNow = LocalDate.now();

    if (!"all".equals(difficulty)) {
      String formattedDifficulty = StringUtils.capitalize(difficulty.toLowerCase());
      try {
        Difficulty diffEnum = Enum.valueOf(Difficulty.class, formattedDifficulty);
        return getTodayProblemsWithDifficulty(
            diffEnum, titleSearch, currentUser, dateNow, pageable);
      } catch (IllegalArgumentException e) {
        throw new GenericNotFoundException("Invalid difficulty.");
      }
    }
    return getTodayProblemsWithoutDifficulty(titleSearch, currentUser, dateNow, pageable);
  }

  private PaginatedSrsProblem getTodayProblemsWithoutDifficulty(
      String titleSearch, User user, LocalDate dateNow, Pageable pageable) {
    Page<SrsProblem> paginatedSrsProblems;

    if (titleSearch != null) {
      paginatedSrsProblems =
          srsProblemRepository
              .findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCase(
                  user, dateNow, titleSearch, pageable);
    } else {
      paginatedSrsProblems =
          srsProblemRepository.findByUserAndNextAttemptAtLessThanEqual(user, dateNow, pageable);
    }

    return new PaginatedSrsProblem(paginatedSrsProblems);
  }

  private PaginatedSrsProblem getTodayProblemsWithDifficulty(
      Difficulty difficulty, String titleSearch, User user, LocalDate dateNow, Pageable pageable) {
    Page<SrsProblem> paginatedSrsProblems;
    if (titleSearch != null) {
      paginatedSrsProblems =
          srsProblemRepository
              .findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCaseAndProblem_Difficulty(
                  user, dateNow, titleSearch, difficulty, pageable);
    } else {
      paginatedSrsProblems =
          srsProblemRepository.findByUserAndNextAttemptAtLessThanEqualAndProblem_Difficulty(
              user, dateNow, difficulty, pageable);
    }
    return new PaginatedSrsProblem(paginatedSrsProblems);
  }

  public Boolean existsByProblemAndUser(Problem problem, User user) {
    return srsProblemRepository.existsByProblemAndUser(problem, user);
  }

  public Optional<SrsProblem> findByProblemAndUser(Problem problem, User user) {
    return srsProblemRepository.findByProblemAndUser(problem, user);
  }

  public Integer countOfProblemsToSolveToday() {
    return srsProblemRepository.countByNextAttemptAtLessThanEqual(LocalDate.now());
  }

  private double initialEaseFactor(Confidence confidence, Problem problem) {
    BigDecimal easeFactor = BigDecimal.valueOf(2.4);

    if (confidence == LOW) easeFactor = easeFactor.subtract(ZERO_POINT_TWO);
    else if (confidence == HIGH) easeFactor = easeFactor.add(ZERO_POINT_TWO);

    if (problem.getDifficulty() == Easy) easeFactor = easeFactor.add(ZERO_POINT_ONE);
    else if (problem.getDifficulty() == Hard) easeFactor = easeFactor.subtract(ZERO_POINT_ONE);

    return Math.min(easeFactor.doubleValue(), 2.6);
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
    BigDecimal gradeBD = BigDecimal.valueOf(grade);
    BigDecimal gradeDiff = FIVE.subtract(gradeBD);

    BigDecimal inner = ZERO_POINT_ZERO_EIGHT.add(gradeDiff.multiply(ZERO_POINT_ZERO_TWO));

    BigDecimal adjustment = ZERO_POINT_ONE.subtract(gradeDiff.multiply(inner));

    BigDecimal result = BigDecimal.valueOf(oldEaseFactor).add(adjustment);

    return result.max(ONE_POINT_THREE).doubleValue();
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
