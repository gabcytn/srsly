package me.gabcytn.srsly.Service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedProblemDto;
import me.gabcytn.srsly.DTO.ProblemDto;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Tag;
import me.gabcytn.srsly.Proxy.LeetCodeQuestionProxy;
import me.gabcytn.srsly.Repository.ProblemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProblemService {
  private final ProblemRepository problemRepository;
  private final HtmlSanitizer htmlSanitizer;
  private final LeetCodeQuestionProxy leetCodeQuestionProxy;
  private final TagService tagService;

  public Problem findByFrontendId(int frontendId) {
    Optional<Problem> nullableProblem = problemRepository.findByFrontendId(frontendId);
    return nullableProblem.orElseGet(() -> fetchAndCacheLeetCodeProblem(frontendId));
  }

  private Problem fetchAndCacheLeetCodeProblem(int id) {
    ProblemDto apiResponse = fetchApi(id);
    sanitizeQuestionContent(apiResponse);
    List<Tag> tags = tagService.saveAll(apiResponse.getTopicTags());
    return problemRepository.save(apiResponse.toProblemEntity(tags));
  }

  private ProblemDto fetchApi(int id) {
    return leetCodeQuestionProxy.getProblem(id);
  }

  private void sanitizeQuestionContent(ProblemDto problem) {
    problem.setContent(htmlSanitizer.sanitize(problem.getContent()));
  }

  public PaginatedProblemDto getAll(int page) {
    Pageable pageable = PageRequest.of(page, 10, Sort.by("frontendId"));
    Page<Problem> problems = problemRepository.findAll(pageable);
    return new PaginatedProblemDto(problems);
  }
}
