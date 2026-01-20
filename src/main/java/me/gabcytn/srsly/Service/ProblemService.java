package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.LeetCodeApiPied;
import me.gabcytn.srsly.DTO.SolutionDto;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Proxy.LeetCodeQuestionProxy;
import me.gabcytn.srsly.Repository.ProblemRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProblemService {
  private final ProblemRepository problemRepository;
  private final HtmlSanitizer htmlSanitizer;
  private final LeetCodeQuestionProxy leetCodeQuestionProxy;
  private final SolutionService solutionService;
  private final UserService userService;

  public LeetCodeApiPied getProblem(int id) {
    Optional<Problem> nullableProblem = problemRepository.findById(id);
    if (nullableProblem.isPresent()) {
      return nullableProblem.get().toApiPied();
    }
    Problem problemFetched = fetchAndCacheLeetCodeProblem(id);
    return problemFetched.toApiPied();
  }

  public void saveSolutionToProblem(SolutionDto solutionDto, int problemId) {
    Optional<Problem> nullableProblem = problemRepository.findById(problemId);
    Problem problem = nullableProblem.orElseGet(() -> fetchAndCacheLeetCodeProblem(problemId));
    solutionService.save(
        solutionDto.toSolutionEntity(problem, userService.getCurrentlyLoggedInUser()));
  }

  private Problem fetchAndCacheLeetCodeProblem(int id) {
    LeetCodeApiPied apiResponse = fetchApi(id);
    sanitizeQuestionContent(apiResponse);
    return problemRepository.save(apiResponse.toProblemEntity());
  }

  private LeetCodeApiPied fetchApi(int id) {
    return leetCodeQuestionProxy.getProblem(id);
  }

  private void sanitizeQuestionContent(LeetCodeApiPied problem) {
    problem.setContent(htmlSanitizer.sanitize(problem.getContent()));
  }
}
