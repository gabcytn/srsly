package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.LeetCodeApiPied;
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

  public Problem findByFrontendId(int frontendId) {
    Optional<Problem> nullableProblem = problemRepository.findById(frontendId);
		return nullableProblem.orElseGet(() -> fetchAndCacheLeetCodeProblem(frontendId));
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
