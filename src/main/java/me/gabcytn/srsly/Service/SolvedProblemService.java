package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.*;
import me.gabcytn.srsly.DTO.Review.InitialProblemReview;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.DTO.Review.ProblemSubmissionWithHistory;
import me.gabcytn.srsly.Entity.*;
import me.gabcytn.srsly.Exception.*;
import me.gabcytn.srsly.Helper.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class SolvedProblemService {
  private final me.gabcytn.srsly.Repository.SolvedProblemRepository solvedProblemRepository;
  private final UserService userService;
  private final ProblemService problemService;
  private final AttemptService attemptService;
  private final SpacedRepetitionHelper spacedRepetitionHelper;

  @Transactional
  public Optional<SolvedProblem> saveInitial(InitialProblemReview initialProblemReview) {
    Problem problem = problemService.findByFrontendId(initialProblemReview.getProblemFrontendId());
    User user = userService.getCurrentUser();
    int untrackedReps = initialProblemReview.getInitialReviewRequest().repetitions();

    ensureProblemNotYetSubmitted(problem, user);

    if (isProblemNotReviewable(initialProblemReview)) {
      return Optional.empty();
    }

    int repetitions = spacedRepetitionHelper.getInitialRepetitions(untrackedReps);
    if (isFreshAttempt(repetitions)) {
      return Optional.of(createFreshInitialAttempt(problem, user));
    }

    ProblemSubmissionWithHistory submission =
        ProblemSubmissionWithHistory.builder()
            .initialReview(initialProblemReview.getInitialReviewRequest())
            .problem(problem)
            .user(user)
            .repetitions(repetitions)
            .build();

    return Optional.of(createFirstSubmissionWithHistory(submission));
  }

  private void ensureProblemNotYetSubmitted(Problem problem, User user) {
    if (existsByProblemAndUser(problem, user)) {
      throw new UnprocessableEntityException("This problem is already solved.");
    }
  }

  private boolean isProblemNotReviewable(InitialProblemReview problemReview) {
    return !problemReview.getIsReviewable();
  }

  private boolean isFreshAttempt(int repetitions) {
    return repetitions == 0;
  }

  private SolvedProblem createFreshInitialAttempt(Problem problem, User user) {
    SolvedProblem solvedProblem = save(SolvedProblem.ofInitial(problem, user));
    createAttemptFromSolvedProblem(solvedProblem);
    return solvedProblem;
  }

  private SolvedProblem createFirstSubmissionWithHistory(ProblemSubmissionWithHistory submission) {
    Integer repetitions = submission.getInitialReview().repetitions();
    Problem problem = submission.getProblem();
    LocalDate lastReviewedAt = submission.getInitialReview().lastReviewedAt();

    double easeFactor = calculateInitialEaseFactor(problem, submission.getInitialReview());
    int interval = calculateInitialInterval(repetitions, easeFactor, lastReviewedAt);
    LocalDate nextReviewDate = calculateNextReviewDate(lastReviewedAt, interval);
    ProblemStatus status = spacedRepetitionHelper.getProblemStatus(repetitions);

    SolvedProblem entity =
        SolvedProblem.builder()
            .status(status)
            .easeFactor(easeFactor)
            .repetitions(repetitions)
            .interval(interval)
            .lastAttemptAt(lastReviewedAt)
            .nextAttemptAt(nextReviewDate)
            .problem(problem)
            .user(submission.getUser())
            .build();
    SolvedProblem solvedProblem = this.save(entity);

    createAttemptFromSolvedProblem(solvedProblem);
    return solvedProblem;
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

    return Math.min(suggestedInterval, adjustedDays);
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
    SolvedProblem solvedProblem = findById(id);

    verifyProblemReviewDate(solvedProblem, dateNow);
    if (reviewFailed(grade)) {
      createAttemptFromFailedReview(solvedProblem, grade);
      return;
    }

    SolvedProblem updatedProblem = updateProblemFromSuccessfulReview(solvedProblem, grade, dateNow);
    createAttemptFromSolvedProblem(updatedProblem, grade);
  }

  private SolvedProblem findById(int id) {
    Optional<SolvedProblem> reviewProblem = solvedProblemRepository.findById(id);
    return reviewProblem.orElseThrow(
        () -> new GenericNotFoundException("Review problem not found."));
  }

  private void verifyProblemReviewDate(SolvedProblem reviewProblem, LocalDate dateNow) {
    if (dateNow.isBefore(reviewProblem.getNextAttemptAt())) {
      throw new EarlyReviewException();
    }
  }

  private boolean reviewFailed(int grade) {
    return grade < 3;
  }

  private void createAttemptFromFailedReview(SolvedProblem solvedProblem, int grade) {
    SolvedProblem created = this.save(spacedRepetitionHelper.reviewFailed(solvedProblem, grade));
    createAttemptFromSolvedProblem(created, grade);
  }

  private SolvedProblem updateProblemFromSuccessfulReview(
      SolvedProblem solvedProblem, int grade, LocalDate dateNow) {
    double easeFactor = spacedRepetitionHelper.calculateEaseFactor(solvedProblem, grade, dateNow);

    solvedProblem.setEaseFactor(easeFactor);
    solvedProblem.setRepetitions(solvedProblem.getRepetitions() + 1);

    int repetitions = solvedProblem.getRepetitions();
    int interval = spacedRepetitionHelper.calculateSubsequentInterval(solvedProblem, dateNow);

    solvedProblem.setStatus(spacedRepetitionHelper.determineProblemStatus(interval, repetitions));
    solvedProblem.setLastAttemptAt(dateNow);
    solvedProblem.setNextAttemptAt(dateNow.plusDays(interval));
    solvedProblem.setInterval(interval);

    return this.save(solvedProblem);
  }

  private void createAttemptFromSolvedProblem(SolvedProblem problem) {
    attemptService.save(Attempt.fromSrsProblem(problem));
  }

  private void createAttemptFromSolvedProblem(SolvedProblem problem, int grade) {
    attemptService.save(Attempt.fromSrsProblem(problem, grade));
  }

  public SolvedProblem save(SolvedProblem solvedProblem) {
    return solvedProblemRepository.save(solvedProblem);
  }

  public PaginatedSolvedProblem getTodayProblems(int page, String difficulty, String titleSearch) {
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

  private PaginatedSolvedProblem getTodayProblemsWithoutDifficulty(
      String titleSearch, User user, LocalDate dateNow, Pageable pageable) {
    Page<SolvedProblem> paginatedSrsProblems;

    if (titleSearch != null) {
      paginatedSrsProblems =
          solvedProblemRepository
              .findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCase(
                  user, dateNow, titleSearch, pageable);
    } else {
      paginatedSrsProblems =
          solvedProblemRepository.findByUserAndNextAttemptAtLessThanEqual(user, dateNow, pageable);
    }

    return new PaginatedSolvedProblem(paginatedSrsProblems);
  }

  private PaginatedSolvedProblem getTodayProblemsWithDifficulty(
      Difficulty difficulty, String titleSearch, User user, LocalDate dateNow, Pageable pageable) {
    Page<SolvedProblem> paginatedSrsProblems;
    if (titleSearch != null) {
      paginatedSrsProblems =
          solvedProblemRepository
              .findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCaseAndProblem_Difficulty(
                  user, dateNow, titleSearch, difficulty, pageable);
    } else {
      paginatedSrsProblems =
          solvedProblemRepository.findByUserAndNextAttemptAtLessThanEqualAndProblem_Difficulty(
              user, dateNow, difficulty, pageable);
    }
    return new PaginatedSolvedProblem(paginatedSrsProblems);
  }

  public Boolean existsByProblemAndUser(Problem problem, User user) {
    return solvedProblemRepository.existsByProblemAndUser(problem, user);
  }

  public Optional<SolvedProblem> findByProblemAndUser(Problem problem, User user) {
    return solvedProblemRepository.findByProblemAndUser(problem, user);
  }

  public Integer countOfProblemsToSolveToday() {
    User user = userService.getCurrentUser();
    return solvedProblemRepository.countByNextAttemptAtLessThanEqualAndUser(LocalDate.now(), user);
  }

  public Page<SolvedProblem> findByUser(User user) {
    Pageable pageable = Pageable.ofSize(10);
    return solvedProblemRepository.findByUser(user, pageable);
  }
}
