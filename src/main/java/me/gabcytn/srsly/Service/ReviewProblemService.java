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
import me.gabcytn.srsly.Publisher.ReviewAttemptEventPublisher;
import me.gabcytn.srsly.Repository.ReviewProblemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class ReviewProblemService
{
  private final ReviewProblemRepository reviewProblemRepository;
  private final ReviewAttemptEventPublisher reviewAttemptEventPublisher;

  public ReviewProblem saveInitialAsReviewable(InitialProblemReview initialProblemReview) {
    SolvedProblem solvedProblem = initialProblemReview.solvedProblem();
    int untrackedReps = initialProblemReview.initialReview().repetitions();

    int repetitions = SpacedRepetitionHelper.normalizeInitialReps(untrackedReps);
    if (isFreshAttempt(repetitions)) {
      return createFreshReviewableInitialAttempt(solvedProblem);
    }

    ProblemSubmissionWithHistory submission =
        ProblemSubmissionWithHistory.builder()
            .initialReview(initialProblemReview.initialReview())
            .solvedProblem(solvedProblem)
            .repetitions(repetitions)
            .build();

    return createFirstSubmissionWithHistory(submission);
  }

  private boolean isFreshAttempt(int repetitions) {
    return repetitions == 0;
  }

  private ReviewProblem createFreshReviewableInitialAttempt(SolvedProblem solvedProblem) {
    ReviewProblem reviewProblem = save(ReviewProblem.ofReviewableInitial(solvedProblem));
    createAttemptFromSolvedProblem(reviewProblem);
    return reviewProblem;
  }

  private ReviewProblem createFirstSubmissionWithHistory(ProblemSubmissionWithHistory submission) {
    Integer repetitions = submission.getInitialReview().repetitions();
    Problem problem = submission.getSolvedProblem().getProblem();
    LocalDate lastReviewedAt = submission.getInitialReview().lastReviewedAt();

    double easeFactor = calculateInitialEaseFactor(problem, submission.getInitialReview());
    int interval = calculateInitialInterval(repetitions, easeFactor, lastReviewedAt);
    LocalDate nextReviewDate = calculateNextReviewDate(lastReviewedAt, interval);
    ProblemStatus status = SpacedRepetitionHelper.getProblemStatus(repetitions);

    ReviewProblem entity =
        ReviewProblem.builder()
            .status(status)
            .easeFactor(easeFactor)
            .repetitions(repetitions)
            .interval(interval)
            .lastAttemptAt(lastReviewedAt)
            .nextAttemptAt(nextReviewDate)
            .solvedProblem(submission.getSolvedProblem())
            .build();
    ReviewProblem reviewProblem = this.save(entity);

    createAttemptFromSolvedProblem(reviewProblem);
    return reviewProblem;
  }

  private double calculateInitialEaseFactor(Problem problem, InitialReviewRequest request) {
    Difficulty difficulty = problem.getDifficulty();
    Confidence confidence = request.confidence();
    return SpacedRepetitionHelper.initialEaseFactor(difficulty, confidence);
  }

  private int calculateInitialInterval(
      int repetitions, double easeFactor, LocalDate lastReviewedAt) {
    int suggestedInterval = SpacedRepetitionHelper.initialInterval(repetitions, easeFactor);
    long daysSinceLastReview = daysSince(lastReviewedAt);

    int adjustedDays = normalizeDaysDifference(daysSinceLastReview);

    return Math.min(suggestedInterval, adjustedDays);
  }

  private long daysSince(LocalDate date) {
    return SpacedRepetitionHelper.dateDifference(date, LocalDate.now());
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
    ReviewProblem reviewProblem = findById(id);

    verifyProblemReviewDate(reviewProblem, dateNow);
    if (reviewFailed(grade)) {
      createAttemptFromFailedReview(reviewProblem, grade);
      return;
    }

    ReviewProblem updatedProblem = updateProblemFromSuccessfulReview(reviewProblem, grade, dateNow);
    createAttemptFromSolvedProblem(updatedProblem, grade);
  }

  private ReviewProblem findById(int id) {
    Optional<ReviewProblem> reviewProblem = reviewProblemRepository.findById(id);
    return reviewProblem.orElseThrow(
        () -> new GenericNotFoundException("Review problem not found."));
  }

  private void verifyProblemReviewDate(ReviewProblem reviewProblem, LocalDate dateNow) {
    if (dateNow.isBefore(reviewProblem.getNextAttemptAt())) {
      throw new EarlyReviewException();
    }
  }

  private boolean reviewFailed(int grade) {
    return grade < 3;
  }

  private void createAttemptFromFailedReview(ReviewProblem reviewProblem, int grade) {
    ReviewProblem created = this.save(SpacedRepetitionHelper.reviewFailed(reviewProblem, grade));
    createAttemptFromSolvedProblem(created, grade);
  }

  private ReviewProblem updateProblemFromSuccessfulReview(
			ReviewProblem reviewProblem, int grade, LocalDate dateNow) {
    double easeFactor = SpacedRepetitionHelper.calculateEaseFactor(reviewProblem, grade, dateNow);

    reviewProblem.setEaseFactor(easeFactor);
    reviewProblem.setRepetitions(reviewProblem.getRepetitions() + 1);

    int repetitions = reviewProblem.getRepetitions();
    int interval = SpacedRepetitionHelper.calculateSubsequentInterval(reviewProblem, dateNow);

    reviewProblem.setStatus(SpacedRepetitionHelper.determineProblemStatus(interval, repetitions));
    reviewProblem.setLastAttemptAt(dateNow);
    reviewProblem.setNextAttemptAt(dateNow.plusDays(interval));
    reviewProblem.setInterval(interval);

    return this.save(reviewProblem);
  }

  private void createAttemptFromSolvedProblem(ReviewProblem problem) {
    reviewAttemptEventPublisher.publish(Attempt.fromSolvedProblem(problem));
  }

  private void createAttemptFromSolvedProblem(ReviewProblem problem, int grade) {
    reviewAttemptEventPublisher.publish(Attempt.fromSolvedProblem(problem, grade));
  }

  public ReviewProblem save(ReviewProblem reviewProblem) {
    return reviewProblemRepository.save(reviewProblem);
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
    Page<ReviewProblem> paginatedSolvedProblem;

    if (titleSearch != null) {
      paginatedSolvedProblem =
          reviewProblemRepository
              .findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCase(
                  user, dateNow, titleSearch, pageable);
    } else {
      paginatedSolvedProblem =
          reviewProblemRepository.findByUserAndNextAttemptAtLessThanEqual(user, dateNow, pageable);
    }

    return new PaginatedSolvedProblem(paginatedSolvedProblem);
  }

  private PaginatedSolvedProblem getTodayProblemsWithDifficulty(
      Difficulty difficulty, String titleSearch, User user, LocalDate dateNow, Pageable pageable) {
    Page<ReviewProblem> paginatedSolvedProblem;
    if (titleSearch != null) {
      paginatedSolvedProblem =
          reviewProblemRepository
              .findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCaseAndProblem_Difficulty(
                  user, dateNow, titleSearch, difficulty, pageable);
    } else {
      paginatedSolvedProblem =
          reviewProblemRepository.findByUserAndNextAttemptAtLessThanEqualAndProblem_Difficulty(
              user, dateNow, difficulty, pageable);
    }
    return new PaginatedSolvedProblem(paginatedSolvedProblem);
  }

  public Boolean existsByProblemAndUser(Problem problem, User user) {
    return reviewProblemRepository.existsByProblemAndUser(problem, user);
  }

  public Optional<ReviewProblem> findByProblemAndUser(Problem problem, User user) {
    return reviewProblemRepository.findByProblemAndUser(problem, user);
  }

  public ReviewProgress getReviewProgress(int solvedTodayCount, User user) {
    LocalDate now = LocalDate.now();
    int unsolvedCount = reviewProblemRepository.countByNextAttemptAtLessThanEqualAndUser(now, user);

    return new ReviewProgress(unsolvedCount, solvedTodayCount);
  }

  public Page<ReviewProblem> findByUser(User user, Pageable pageable) {
    return reviewProblemRepository.findByUser(user, pageable);
  }
}
