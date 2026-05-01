package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedSolvedProblem;
import me.gabcytn.srsly.DTO.Problem.ProblemDetailDto;
import me.gabcytn.srsly.DTO.Problem.ReviewDetail;
import me.gabcytn.srsly.DTO.Review.InitialProblemReview;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.DTO.ReviewProgress;
import me.gabcytn.srsly.DTO.ReviewableProblemsFilter;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.ReviewProblem;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.UnprocessableEntityException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProblemFacadeService {
  private final AttemptService attemptService;
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
      reviewDetail = new ReviewDetail(reviewProblem.getId(), reviewProblem.getNextAttemptAt());
      problemDetail.setIsSolved(Boolean.TRUE);
    }
    problemDetail.setReviewDetail(reviewDetail);

    return problemDetail;
  }

  public PaginatedSolvedProblem findProblemsSolvedByUser(int pageNumber) {
    User user = userService.getCurrentUser();
    Pageable pageRequest = PageRequest.of(pageNumber, 10);
    return new PaginatedSolvedProblem(reviewProblemService.findByUser(user, pageRequest));
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
    int reviewedProblemsCount = attemptService.getCountOfReviewedProblemsToday(user);
    return reviewProblemService.getReviewProgress(reviewedProblemsCount, user);
  }

  public PaginatedSolvedProblem getProblemsToReviewToday(ReviewableProblemsFilter filters) {
    return reviewProblemService.getTodayProblems(filters, userService.getCurrentUser());
  }
}
