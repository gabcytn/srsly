package me.gabcytn.srsly.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.ProblemDto;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Service.ProblemService;
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

  @GetMapping("/{id}")
  @JsonView(Views.Detailed.class)
  public ProblemDto getProblem(@PathVariable int id) {
    Problem problem = problemService.findByFrontendId(id);
    Boolean isSolved = srsProblemService.existsByProblemAndUser(problem, userService.getCurrentlyLoggedInUser());

    ProblemDto dto = problem.toApiPied();
    dto.setIsSolved(isSolved);
    return dto;
  }
}
