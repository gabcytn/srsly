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
import me.gabcytn.srsly.Repository.SolvedProblemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class SolvedProblemService {
  private final SolvedProblemRepository solvedProblemRepository;
  private final AttemptService attemptService;
  private final SpacedRepetitionHelper spacedRepetitionHelper;

  public SolvedProblem saveInitialAsNonReviewable(Problem problem, User user) {
    ensureProblemNotYetSubmitted(problem, user);
    return save(SolvedProblem.ofNonReviewableInitial(problem, user));
  }

  public SolvedProblem saveInitialAsReviewable(InitialProblemReview initialProblemReview) {
    Problem problem = initialProblemReview.problem();
    User user = initialProblemReview.user();
    int untrackedReps = initialProblemReview.initialReview().repetitions();

    ensureProblemNotYetSubmitted(problem, user);
    int repetitions = spacedRepetitionHelper.getInitialRepetitions(untrackedReps);
    if (isFreshAttempt(repetitions)) {
      return createFreshReviewableInitialAttempt(problem, user);
    }

    ProblemSubmissionWithHistory submission =
        ProblemSubmissionWithHistory.builder()
            .initialReview(initialProblemReview.initialReview())
            .problem(problem)
            .user(user)
            .repetitions(repetitions)
            .build();

    return createFirstSubmissionWithHistory(submission);
  }

  private void ensureProblemNotYetSubmitted(Problem problem, User user) {
    if (existsByProblemAndUser(problem, user)) {
      throw new UnprocessableEntityException("This problem is already solved.");
    }
  }

  private boolean isFreshAttempt(int repetitions) {
    return repetitions == 0;
  }

  private SolvedProblem createFreshReviewableInitialAttempt(Problem problem, User user) {
    SolvedProblem solvedProblem = save(SolvedProblem.ofReviewableInitial(problem, user));
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
    attemptService.save(Attempt.fromSolvedProblem(problem));
  }

  private void createAttemptFromSolvedProblem(SolvedProblem problem, int grade) {
    attemptService.save(Attempt.fromSolvedProblem(problem, grade));
  }

  public SolvedProblem save(SolvedProblem solvedProblem) {
    return solvedProblemRepository.save(solvedProblem);
  }

  public PaginatedSolvedProblem getTodayProblems(ReviewableProblemsFilter filter, User currentUser) {
    Pageable pageable = PageRequest.of(filter.getPage(), 5, Sort.by("nextAttemptAt"));
    LocalDate dateNow = LocalDate.now();

    if (!"all".equals(filter.getDifficulty())) {
      String formattedDifficulty = StringUtils.capitalize(filter.getDifficulty().toLowerCase());
      try {
        Difficulty diffEnum = Enum.valueOf(Difficulty.class, formattedDifficulty);
        return getTodayProblemsWithDifficulty(
            diffEnum, filter.getTitle(), currentUser, dateNow, pageable);
      } catch (IllegalArgumentException e) {
        throw new GenericNotFoundException("Invalid difficulty.");
      }
    }
    return getTodayProblemsWithoutDifficulty(filter.getTitle(), currentUser, dateNow, pageable);
  }

  private PaginatedSolvedProblem getTodayProblemsWithoutDifficulty(
      String titleSearch, User user, LocalDate dateNow, Pageable pageable) {
    Page<SolvedProblem> paginatedSolvedProblem;

    if (titleSearch != null) {
      paginatedSolvedProblem =
          solvedProblemRepository
              .findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCase(
                  user, dateNow, titleSearch, pageable);
    } else {
      paginatedSolvedProblem =
          solvedProblemRepository.findByUserAndNextAttemptAtLessThanEqual(user, dateNow, pageable);
    }

    return new PaginatedSolvedProblem(paginatedSolvedProblem);
  }

  private PaginatedSolvedProblem getTodayProblemsWithDifficulty(
      Difficulty difficulty, String titleSearch, User user, LocalDate dateNow, Pageable pageable) {
    Page<SolvedProblem> paginatedSolvedProblem;
    if (titleSearch != null) {
      paginatedSolvedProblem =
          solvedProblemRepository
              .findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCaseAndProblem_Difficulty(
                  user, dateNow, titleSearch, difficulty, pageable);
    } else {
      paginatedSolvedProblem =
          solvedProblemRepository.findByUserAndNextAttemptAtLessThanEqualAndProblem_Difficulty(
              user, dateNow, difficulty, pageable);
    }
    return new PaginatedSolvedProblem(paginatedSolvedProblem);
  }

  public Boolean existsByProblemAndUser(Problem problem, User user) {
    return solvedProblemRepository.existsByProblemAndUser(problem, user);
  }

  public Optional<SolvedProblem> findByProblemAndUser(Problem problem, User user) {
    return solvedProblemRepository.findByProblemAndUser(problem, user);
  }

  public ReviewProgress getReviewProgress(User user) {
    LocalDate now = LocalDate.now();

    int solvedTodayCount = attemptService.countSolvedTodayExcludingInitial(user);
    int unsolvedCount = solvedProblemRepository.countByNextAttemptAtLessThanEqualAndUser(now, user);

    return new ReviewProgress(unsolvedCount, solvedTodayCount);
  }

  public Page<SolvedProblem> findByUser(User user, Pageable pageable) {
    return solvedProblemRepository.findByUser(user, pageable);
  }
}
