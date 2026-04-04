package me.gabcytn.srsly.Controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedProblemDto;
import me.gabcytn.srsly.DTO.Problem.ProblemDetailDto;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;
import me.gabcytn.srsly.Service.ProblemFacadeService;
import me.gabcytn.srsly.Service.ProblemService;
import me.gabcytn.srsly.Service.ProblemSuggestionService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {
  private final ProblemService problemService;
  private final ProblemFacadeService problemFacadeService;
  private final ProblemSuggestionService problemSuggestionService;

  @GetMapping
  public PaginatedProblemDto getAll(
      @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
    return problemService.getAll(page);
  }

  @GetMapping("/suggested")
  public List<ProblemSummaryDto> getSuggestedProblems() {
    return problemSuggestionService.getSuggestions();
  }

  @GetMapping("/{id}")
  public ProblemDetailDto getProblem(@PathVariable int id) {
    return problemFacadeService.findDtoByFrontendId(id);
  }
}
