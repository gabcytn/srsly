package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.ProblemDto;
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
    Optional<Problem> nullableProblem = problemRepository.findByFrontendId(frontendId);
		return nullableProblem.orElseGet(() -> fetchAndCacheLeetCodeProblem(frontendId));
	}

  private Problem fetchAndCacheLeetCodeProblem(int id) {
    ProblemDto apiResponse = fetchApi(id);
    sanitizeQuestionContent(apiResponse);
    return problemRepository.save(apiResponse.toProblemEntity());
  }

  private ProblemDto fetchApi(int id) {
    return leetCodeQuestionProxy.getProblem(id);
  }

  private void sanitizeQuestionContent(ProblemDto problem) {
    problem.setContent(htmlSanitizer.sanitize(problem.getContent()));
  }
}
