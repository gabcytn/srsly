package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.Problem.ProblemDetailDto;
import me.gabcytn.srsly.DTO.Problem.ReviewDetail;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Entity.User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProblemFacadeService {
  private final SrsProblemService srsProblemService;
  private final ProblemService problemService;
  private final UserService userService;

  public ProblemDetailDto findDtoByFrontendId(int frontendId) {
    Problem problem = problemService.findByFrontendId(frontendId);
    User user = userService.getCurrentUser();
    Optional<SrsProblem> optional = srsProblemService.findByProblemAndUser(problem, user);

    ProblemDetailDto problemDetail = new ProblemDetailDto(problem.summarize());
    problemDetail.setIsSolved(optional.isPresent());

    ReviewDetail reviewDetail = null;
    if (optional.isPresent()) {
      SrsProblem srsProblem = optional.get();
      reviewDetail = new ReviewDetail(srsProblem.getId(), srsProblem.getNextAttemptAt());
    }
    problemDetail.setReviewDetail(reviewDetail);

    return problemDetail;
  }
}
