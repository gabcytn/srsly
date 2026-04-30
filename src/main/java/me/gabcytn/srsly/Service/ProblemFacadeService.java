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
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProblemFacadeService {
  private final SolvedProblemService solvedProblemService;
  private final ProblemService problemService;
  private final UserService userService;

  public ProblemDetailDto findDtoByFrontendId(int frontendId) {
    Problem problem = problemService.findByFrontendId(frontendId);
    User user = userService.getCurrentUser();
    Optional<SolvedProblem> optional = solvedProblemService.findByProblemAndUser(problem, user);

    ProblemDetailDto problemDetail =
        new ProblemDetailDto(problem.summarize(), problem.getQuestion());

    ReviewDetail reviewDetail = null;
    if (optional.isPresent()) {
      SolvedProblem solvedProblem = optional.get();
      reviewDetail = new ReviewDetail(solvedProblem.getId(), solvedProblem.getNextAttemptAt());
      problemDetail.setIsSolved(Boolean.TRUE);
    }
    problemDetail.setReviewDetail(reviewDetail);

    return problemDetail;
  }

  public PaginatedSolvedProblem findProblemsSolvedByUser(int pageNumber) {
    User user = userService.getCurrentUser();
    Pageable pageRequest = PageRequest.of(pageNumber, 10);
    return new PaginatedSolvedProblem(solvedProblemService.findByUser(user, pageRequest));
  }

  @Transactional
  public SolvedProblem saveInitialAsReviewable(InitialReviewRequest reviewRequest, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentUser();

    return solvedProblemService.saveInitialAsReviewable(
        new InitialProblemReview(reviewRequest, problem, user));
  }

  public SolvedProblem saveInitialAsNonReviewable(int problemFrontendId) {
    Problem problem = problemService.findByFrontendId(problemFrontendId);
    User user = userService.getCurrentUser();

    return solvedProblemService.saveInitialAsNonReviewable(problem, user);
  }

  @Transactional
  public ReviewProgress getReviewProgress() {
    User user = userService.getCurrentUser();
    return solvedProblemService.getReviewProgress(user);
  }

  public PaginatedSolvedProblem getProblemsToReviewToday(ReviewableProblemsFilter filters) {
    return solvedProblemService.getTodayProblems(filters, userService.getCurrentUser());
  }
}
