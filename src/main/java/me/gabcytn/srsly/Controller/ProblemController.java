package me.gabcytn.srsly.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.LeetCodeApiPied;
import me.gabcytn.srsly.DTO.SolutionDto;
import me.gabcytn.srsly.Service.ProblemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {
  private final ProblemService problemService;

  @GetMapping("/{id}")
  public LeetCodeApiPied getProblem(@PathVariable int id) {
    return problemService.getProblem(id);
  }

  @PostMapping("/{problemId}/solutions")
  public ResponseEntity<Void> saveSolution(
      @PathVariable int problemId, @RequestBody @Valid SolutionDto solution) {
    problemService.saveSolutionToProblem(solution, problemId);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
