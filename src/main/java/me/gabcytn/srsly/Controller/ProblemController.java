package me.gabcytn.srsly.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedProblemDto;
import me.gabcytn.srsly.DTO.ProblemDto;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Service.ProblemService;
import me.gabcytn.srsly.Service.ProblemSuggestionService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {
  private final ProblemService problemService;
  private final ProblemSuggestionService problemSuggestionService;

  @GetMapping
  @JsonView(Views.Summary.class)
  public PaginatedProblemDto getAll(
      @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
    return problemService.getAll(page);
  }

  @GetMapping("/suggested")
  @JsonView(Views.Summary.class)
  public List<ProblemDto> getSuggestedProblems() {
    return problemSuggestionService.getSuggestions();
  }

  @GetMapping("/{id}")
  @JsonView(Views.Detailed.class)
  public ProblemDto getProblem(@PathVariable int id) {
    return problemService.findDtoByFrontendId(id);
  }
}
