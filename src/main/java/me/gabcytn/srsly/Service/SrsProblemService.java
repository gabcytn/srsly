package me.gabcytn.srsly.Service;

import java.time.LocalDate;
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
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class SrsProblemService {
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
    return spacedRepetitionHelper.dateDifference(date, LocalDate.now());
  }

  private int normalizeDaysDifference(long days) {
    return (days == 0) ? 1 : (int) days;
  }

  private LocalDate calculateNextReviewDate(LocalDate lastReviewedAt, int interval) {
    return lastReviewedAt.plusDays(interval);
  }

  @Transactional
  public void saveSubsequent(int id, int grade) {
    LocalDate dateNow = LocalDate.now();
    SrsProblem srsProblem = findById(id);

    verifyProblemReviewDate(srsProblem, dateNow);
    if (reviewFailed(grade)) {
      createAttemptFromFailedReview(srsProblem, grade);
      return;
    }

    double easeFactor = spacedRepetitionHelper.calculateEaseFactor(srsProblem, grade, dateNow);

    srsProblem.setEaseFactor(easeFactor);
    srsProblem.setRepetitions(srsProblem.getRepetitions() + 1);

    int repetitions = srsProblem.getRepetitions();
    int interval = spacedRepetitionHelper.calculateSubsequentInterval(srsProblem, dateNow);

    srsProblem.setStatus(spacedRepetitionHelper.determineProblemStatus(interval, repetitions));
    srsProblem.setLastAttemptAt(dateNow);
    srsProblem.setNextAttemptAt(dateNow.plusDays(interval));
    srsProblem.setInterval(interval);

    SrsProblem savedProblem = this.save(srsProblem);
    attemptService.save(Attempt.fromSrsProblem(savedProblem, grade));
  }

  private SrsProblem findById(int id) {
    Optional<SrsProblem> reviewProblem = srsProblemRepository.findById(id);
    return reviewProblem.orElseThrow(
        () -> new GenericNotFoundException("Review problem not found."));
  }

  private void verifyProblemReviewDate(SrsProblem reviewProblem, LocalDate dateNow) {
    if (dateNow.isBefore(reviewProblem.getNextAttemptAt())) {
      throw new EarlyReviewException();
    }
  }

  private boolean reviewFailed(int grade) {
    return grade < 3;
  }

  private void createAttemptFromFailedReview(SrsProblem srsProblem, int grade) {
    SrsProblem created = this.save(spacedRepetitionHelper.reviewFailed(srsProblem, grade));
    attemptService.save(Attempt.fromSrsProblem(created, grade));
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
}
