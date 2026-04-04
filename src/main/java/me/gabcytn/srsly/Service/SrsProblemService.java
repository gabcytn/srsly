package me.gabcytn.srsly.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.*;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.Entity.*;
import me.gabcytn.srsly.Exception.*;
import me.gabcytn.srsly.Helper.*;
import me.gabcytn.srsly.Repository.SrsProblemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
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
  private final ProblemService problemService;
  private final AttemptService attemptService;
  private final SpacedRepetitionHelper spacedRepetitionHelper;

  public void saveInitial(InitialReviewRequest request, Integer frontendId) {
    Problem problem = problemService.findByFrontendId(frontendId);
    User user = userService.getCurrentUser();

    ensureProblemNotYetSubmitted(problem, user);

    int repetitions = spacedRepetitionHelper.getInitialRepetitions(request.repetitions());

    if (isFreshAttempt(repetitions)) {
      createFreshInitialAttempt(problem, user);
      return;
    }

    createFirstSubmissionWithHistory(request, problem, user, repetitions);
  }

  private void ensureProblemNotYetSubmitted(Problem problem, User user) {
    if (existsByProblemAndUser(problem, user)) {
      throw new UnprocessableEntityException("This problem is already solved.");
    }
  }

  private boolean isFreshAttempt(int repetitions) {
    return repetitions == 0;
  }

  private void createFreshInitialAttempt(Problem problem, User user) {
    SrsProblem srsProblem = save(SrsProblem.ofInitial(problem, user));
    attemptService.save(Attempt.fromSrsProblem(srsProblem));
  }

  private void createFirstSubmissionWithHistory(
      InitialReviewRequest request, Problem problem, User user, int repetitions) {
    LocalDate lastReviewedAt = request.lastReviewedAt();

    double easeFactor = calculateInitialEaseFactor(problem, request);
    int interval = calculateInitialInterval(repetitions, easeFactor, lastReviewedAt);
    LocalDate nextReviewDate = calculateNextReviewDate(lastReviewedAt, interval);
    ProblemStatus status = spacedRepetitionHelper.getProblemStatus(repetitions);

    SrsProblem entity =
        new SrsProblemEntityBuilder()
            .status(status)
            .easeFactor(easeFactor)
            .repetitions(repetitions)
            .interval(interval)
            .lastAttemptAt(lastReviewedAt)
            .nextAttemptAt(nextReviewDate)
            .problem(problem)
            .user(user)
            .build();
    SrsProblem srsProblem = this.save(entity);

    attemptService.save(Attempt.fromSrsProblem(srsProblem));
  }

  private double calculateInitialEaseFactor(Problem problem, InitialReviewRequest request) {
    Difficulty difficulty = problem.getDifficulty();
    Confidence confidence = request.confidence();
    return spacedRepetitionHelper.initialEaseFactor(difficulty, confidence);
  }

  private int calculateInitialInterval(
      int repetitions, double easeFactor, LocalDate lastReviewedAt) {
    int suggestedInterval = spacedRepetitionHelper.initialInterval(repetitions, easeFactor);
    long daysSinceLastReview = daysSince(lastReviewedAt);

    int adjustedDays = normalizeDaysDifference(daysSinceLastReview);

    return (int) Math.min(suggestedInterval, adjustedDays);
  }

  private long daysSince(LocalDate date) {
    return dateDifference(date, LocalDate.now());
  }

  private int normalizeDaysDifference(long days) {
    return (days == 0) ? 1 : (int) days;
  }

  private LocalDate calculateNextReviewDate(LocalDate lastReviewedAt, int interval) {
    return lastReviewedAt.plusDays(interval);
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
    User currentUser = userService.getCurrentUser();
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
    User user = userService.getCurrentUser();
    return srsProblemRepository.countByNextAttemptAtLessThanEqualAndUser(LocalDate.now(), user);
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
