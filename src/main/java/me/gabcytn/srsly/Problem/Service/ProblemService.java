package me.gabcytn.srsly.Problem.Service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Exception.ResourceNotFoundException;
import me.gabcytn.srsly.Problem.DTO.LeetCodeProblemApiResponse;
import me.gabcytn.srsly.Problem.DTO.PaginatedProblemDto;
import me.gabcytn.srsly.Problem.Entity.Problem;
import me.gabcytn.srsly.Problem.Entity.Tag;
import me.gabcytn.srsly.Problem.Proxy.LeetCodeQuestionProxy;
import me.gabcytn.srsly.Problem.Repository.ProblemRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProblemService {
  private final ProblemRepository problemRepository;
  private final HtmlSanitizerService htmlSanitizerService;
  private final LeetCodeQuestionProxy leetCodeQuestionProxy;
  private final TagService tagService;

  public Problem findByFrontendId(int frontendId) {
    Optional<Problem> nullableProblem = problemRepository.findByFrontendId(frontendId);
    return nullableProblem.orElseGet(() -> fetchAndCacheLeetCodeProblem(frontendId));
  }

  private Problem fetchAndCacheLeetCodeProblem(int id) {
    LeetCodeProblemApiResponse apiResponse = fetchApi(id);
    sanitizeQuestionContent(apiResponse);
    List<Tag> tags = tagService.saveAll(apiResponse.getTopicTags());
    return problemRepository.save(apiResponse.toProblemEntity(tags));
  }

  private LeetCodeProblemApiResponse fetchApi(int id) {
    try {
      return leetCodeQuestionProxy.getProblem(id);
    } catch (Exception e) {
      log.error("Error fetching problem from API with message: {}", e.getMessage());
      throw new ResourceNotFoundException("Problem not found.");
    }
  }

  private void sanitizeQuestionContent(LeetCodeProblemApiResponse problem) {
    problem.setContent(htmlSanitizerService.sanitize(problem.getContent()));
  }

  public PaginatedProblemDto getAll(int page) {
    Pageable pageable = PageRequest.of(page, 10, Sort.by("frontendId"));
    Page<Problem> problems = problemRepository.findAll(pageable);
    return new PaginatedProblemDto(problems);
  }
}
