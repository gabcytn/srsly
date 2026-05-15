package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.DTO.PaginatedReviewProblem;
import me.gabcytn.srsly.DTO.PaginatedSolvedProblem;
import me.gabcytn.srsly.DTO.Problem.ProblemDetailDto;
import me.gabcytn.srsly.DTO.Problem.ReviewDetail;
import me.gabcytn.srsly.DTO.ProblemSearchFilter;
import me.gabcytn.srsly.DTO.Review.InitialProblemReview;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.DTO.ReviewProgress;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.ReviewProblem;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.UnprocessableEntityException;
import me.gabcytn.srsly.Repository.Specification.ProblemSearchSpecification;
import me.gabcytn.srsly.Repository.Specification.ReviewProblemSpecification;
import me.gabcytn.srsly.Repository.Specification.SolvedProblemSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProblemFacadeService {
  private final ReviewAttemptService reviewAttemptService;
  private final ReviewProblemService reviewProblemService;
  private final ProblemService problemService;
  private final SolvedProblemService solvedProblemService;
  private final UserService userService;

  public ProblemDetailDto findDtoByFrontendId(int frontendId) {
    Problem problem = problemService.findByFrontendId(frontendId);
    User user = userService.getCurrentUser();
    Optional<ReviewProblem> optional = reviewProblemService.findByProblemAndUser(problem, user);

    ProblemDetailDto problemDetail =
        new ProblemDetailDto(problem.summarize(), problem.getQuestion());

    ReviewDetail reviewDetail = null;
    if (optional.isPresent()) {
      ReviewProblem reviewProblem = optional.get();
      reviewDetail =
          new ReviewDetail(
              reviewProblem.getId(),
              reviewProblem.getLastAttemptAt(),
              reviewProblem.getNextAttemptAt(),
              reviewProblem.getStatus());
      problemDetail.setIsSolved(Boolean.TRUE);
    }
    problemDetail.setReviewDetail(reviewDetail);

    return problemDetail;
  }

  @Transactional
  public ReviewProblem saveInitialAsReviewable(InitialReviewRequest reviewRequest, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentUser();
    ensureProblemNotYetSubmitted(problem, user);

    SolvedProblem solvedProblem = new SolvedProblem(problem, user);
    solvedProblemService.save(solvedProblem);

    return reviewProblemService.saveInitialAsReviewable(
        new InitialProblemReview(reviewRequest, solvedProblem));
  }

  private void ensureProblemNotYetSubmitted(Problem problem, User user) {
    if (solvedProblemService.existsByProblemAndUser(problem, user)) {
      throw new UnprocessableEntityException("Problem already solved.");
    }
  }

  public void saveInitialAsNonReviewable(int problemFrontendId) {
    Problem problem = problemService.findByFrontendId(problemFrontendId);
    User user = userService.getCurrentUser();

    ensureProblemNotYetSubmitted(problem, user);
    solvedProblemService.save(new SolvedProblem(problem, user));
  }

  @Transactional
  public ReviewProgress getReviewProgress() {
    User user = userService.getCurrentUser();
    int reviewedProblemsCount = reviewAttemptService.getCountOfReviewedProblemsToday(user);
    return reviewProblemService.getReviewProgress(reviewedProblemsCount, user);
  }

  public PaginatedReviewProblem getProblemsToReviewToday(ProblemSearchFilter filters) {
    User user = userService.getCurrentUser();
    Specification<ReviewProblem> spec = Specification.unrestricted();

    String difficulty = filters.getDifficulty();
    String title = filters.getTitle();

    ReviewProblemSpecification specBuilder = new ReviewProblemSpecification();
    spec = spec.and(specBuilder.hasUser(user));

    if (!title.isBlank()) {
      spec = spec.and(specBuilder.hasTitle(title));
    }

    if (!difficulty.equalsIgnoreCase("all")) {
      spec = spec.and(specBuilder.hasDifficulty(difficulty));
    }

    spec = spec.and(specBuilder.hasNextAttemptAtLessThanOrEqualTo(LocalDate.now()));

    return reviewProblemService.getReviewProblemsToday(spec, user, filters.getPage());
  }

  public PaginatedSolvedProblem findProblemsSolvedByUser(ProblemSearchFilter filters) {
    User user = userService.getCurrentUser();
    Specification<SolvedProblem> spec = Specification.unrestricted();

    String title = filters.getTitle();
    String difficulty = filters.getDifficulty();

    ProblemSearchSpecification<SolvedProblem> specBuilder = new SolvedProblemSpecification();
    spec = spec.and(specBuilder.hasUser(user));

    if (!title.isBlank()) {
      spec = spec.and(specBuilder.hasTitle(title));
    }

    if (!difficulty.equalsIgnoreCase("all")) {
      spec = spec.and(specBuilder.hasDifficulty(difficulty));
    }

    return solvedProblemService.findByUser(spec, user, filters.getPage());
  }
}
