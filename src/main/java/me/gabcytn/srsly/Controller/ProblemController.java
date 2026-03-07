package me.gabcytn.srsly.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedProblemDto;
import me.gabcytn.srsly.DTO.ProblemDto;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Service.ProblemService;
import me.gabcytn.srsly.Service.ProblemSuggestionService;
import me.gabcytn.srsly.Service.SrsProblemService;
import me.gabcytn.srsly.Service.UserService;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {
  private final ProblemService problemService;
  private final UserService userService;
  private final SrsProblemService srsProblemService;
  private final ProblemSuggestionService problemSuggestionService;

  @GetMapping
  @JsonView(Views.Summary.class)
  public PaginatedProblemDto getAll(@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
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
    Problem problem = problemService.findByFrontendId(id);
    Optional<SrsProblem> found = srsProblemService.findByProblemAndUser(problem, userService.getCurrentlyLoggedInUser());

    ProblemDto dto = problem.toApiPied();
    dto.setIsSolved(found.isPresent());
		found.ifPresent(srsProblem -> {
      dto.setSrsId(srsProblem.getId());
      dto.setNextAttemptAt(srsProblem.getNextAttemptAt());
    });
    return dto;
  }
}
