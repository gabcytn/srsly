package me.gabcytn.srsly.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.DTO.ProblemDto;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Proxy.LeetCodeQuestionProxy;
import me.gabcytn.srsly.Service.ProblemService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {

  private final ProblemService problemService;
  private final LeetCodeQuestionProxy leetCodeQuestionProxy;

  @GetMapping("/{id}")
  @JsonView(Views.Detailed.class)
  public ProblemDto getProblem(@PathVariable int id) {
    Problem problem = problemService.findByFrontendId(id);
    return problem.toApiPied();
  }

  @GetMapping("/straight/{id}")
  @JsonView(Views.Summary.class)
  public ProblemDto straight(@PathVariable int id) {
    ProblemDto dto = leetCodeQuestionProxy.getProblem(id);
    log.info("Problem: {}", dto);
    return dto;
  }
}
