package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedSolvedProblem;
import me.gabcytn.srsly.DTO.Problem.ProblemDetailDto;
import me.gabcytn.srsly.DTO.Problem.ReviewDetail;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import org.springframework.stereotype.Service;

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

  public PaginatedSolvedProblem findProblemsSolvedByUser() {
    User user = userService.getCurrentUser();
    return new PaginatedSolvedProblem(solvedProblemService.findByUser(user));
  }
}
