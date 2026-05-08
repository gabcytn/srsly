package me.gabcytn.srsly.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedProblemDto;
import me.gabcytn.srsly.DTO.Problem.ProblemDetailDto;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;
import me.gabcytn.srsly.Service.ProblemFacadeService;
import me.gabcytn.srsly.Service.ProblemService;
import me.gabcytn.srsly.Service.ProblemSuggestionService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "LeetCode Problems")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {
  private final ProblemService problemService;
  private final ProblemFacadeService problemFacadeService;
  private final ProblemSuggestionService problemSuggestionService;

  @Operation(summary = "All Problems in Database")
  @GetMapping
  public PaginatedProblemDto getAll(
      @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
    return problemService.getAll(page);
  }

  @Operation(summary = "Suggested problems")
  @GetMapping("/suggested")
  public List<ProblemSummaryDto> getSuggestedProblems() {
    return problemSuggestionService.getSuggestions();
  }

  @Operation(summary = "Problem details")
  @GetMapping("/{id}")
  public ProblemDetailDto getProblem(@PathVariable int id) {
    return problemFacadeService.findDtoByFrontendId(id);
  }
}
