package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.*;
import me.gabcytn.srsly.DTO.Review.InitialProblemReview;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.DTO.Review.ProblemSubmissionWithHistory;
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

  @Transactional
  public Optional<SrsProblem> saveInitial(InitialProblemReview initialProblemReview) {
    Problem problem = problemService.findByFrontendId(initialProblemReview.getProblemFrontendId());
    User user = userService.getCurrentUser();
    Integer repetitions = initialProblemReview.getInitialReviewRequest().repetitions();

    ensureProblemNotYetSubmitted(problem, user);
    markProblemAsSolvedBy(problem, user);

    if (isProblemNotReviewable(initialProblemReview)) {
      return Optional.empty();
    }

    repetitions = spacedRepetitionHelper.getInitialRepetitions(repetitions);

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
    if (isProblemSolvedBy(problem, user)) {
      throw new UnprocessableEntityException("This problem is already solved.");
    }
  }

  private void markProblemAsSolvedBy(Problem problem, User user) {
    Set<Problem> solvedProblems = user.getSolvedProblems();
    solvedProblems.add(problem);
    user.setSolvedProblems(solvedProblems);
    userService.save(user);
  }

  private boolean isProblemNotReviewable(InitialProblemReview problemReview) {
    return !problemReview.getIsReviewable();
  }

  private boolean isFreshAttempt(int repetitions) {
    return repetitions == 0;
  }

  private SrsProblem createFreshInitialAttempt(Problem problem, User user) {
    SrsProblem srsProblem = save(SrsProblem.ofInitial(problem, user));
    createAttemptFromSrsProblem(srsProblem);
    return srsProblem;
  }

  private SrsProblem createFirstSubmissionWithHistory(ProblemSubmissionWithHistory submission) {
    Integer repetitions = submission.getInitialReview().repetitions();
    Problem problem = submission.getProblem();
    LocalDate lastReviewedAt = submission.getInitialReview().lastReviewedAt();

    double easeFactor = calculateInitialEaseFactor(problem, submission.getInitialReview());
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
            .user(submission.getUser())
            .build();
    SrsProblem srsProblem = this.save(entity);

    createAttemptFromSrsProblem(srsProblem);
    return srsProblem;
  }

  private boolean isProblemSolvedBy(Problem problem, User user) {
    return user.getSolvedProblems().contains(problem);
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

    SrsProblem updatedProblem = updateProblemFromSuccessfulReview(srsProblem, grade, dateNow);
    createAttemptFromSrsProblem(updatedProblem, grade);
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
    createAttemptFromSrsProblem(created, grade);
  }

  private SrsProblem updateProblemFromSuccessfulReview(
      SrsProblem srsProblem, int grade, LocalDate dateNow) {
    double easeFactor = spacedRepetitionHelper.calculateEaseFactor(srsProblem, grade, dateNow);

    srsProblem.setEaseFactor(easeFactor);
    srsProblem.setRepetitions(srsProblem.getRepetitions() + 1);

    int repetitions = srsProblem.getRepetitions();
    int interval = spacedRepetitionHelper.calculateSubsequentInterval(srsProblem, dateNow);

    srsProblem.setStatus(spacedRepetitionHelper.determineProblemStatus(interval, repetitions));
    srsProblem.setLastAttemptAt(dateNow);
    srsProblem.setNextAttemptAt(dateNow.plusDays(interval));
    srsProblem.setInterval(interval);

    return this.save(srsProblem);
  }

  private void createAttemptFromSrsProblem(SrsProblem problem) {
    attemptService.save(Attempt.fromSrsProblem(problem));
  }

  private void createAttemptFromSrsProblem(SrsProblem problem, int grade) {
    attemptService.save(Attempt.fromSrsProblem(problem, grade));
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
