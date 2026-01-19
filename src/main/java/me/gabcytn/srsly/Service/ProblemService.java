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

  public LeetCodeApiPied getProblem(int id) {
    Optional<Problem> nullableProblem = problemRepository.findById(id);
    if (nullableProblem.isPresent()) {
      return nullableProblem.get().toApiPied();
    }
    LeetCodeApiPied apiResponse = fetchApi(id);
    sanitizeQuestionContent(apiResponse);
    problemRepository.save(apiResponse.toProblemEntity());
    return apiResponse;
  }

  private LeetCodeApiPied fetchApi(int id) {
		return leetCodeQuestionProxy.getProblem(id);
  }

  private void sanitizeQuestionContent(LeetCodeApiPied problem) {
    problem.setContent(htmlSanitizer.sanitize(problem.getContent()));
  }
}
