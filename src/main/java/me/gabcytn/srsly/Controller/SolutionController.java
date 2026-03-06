package me.gabcytn.srsly.Controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.EditSolution;
import me.gabcytn.srsly.DTO.InitialSolutionDto;
import me.gabcytn.srsly.DTO.SolutionDto;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Service.SolutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class SolutionController {
  private final SolutionService solutionService;

  @PostMapping("/problems/{problemId}/solutions")
  public ResponseEntity<SolutionDto> save(
      @PathVariable int problemId, @RequestBody @Valid SolutionDto solutionDto) {
    Solution s = solutionService.saveToProblem(solutionDto, problemId);
    return new ResponseEntity<>(s.toDto(), HttpStatus.CREATED);
  }

  @PostMapping("/problems/{problemId}/solutions/initial")
  public ResponseEntity<Void> initialSolution(
      @PathVariable int problemId, @RequestBody @Valid InitialSolutionDto initialSolutionDto) {
    solutionService.saveInitialToProblem(initialSolutionDto, problemId);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/problems/{problemId}/solutions")
  public List<SolutionDto> getSolutions(@PathVariable int problemId) {
    return solutionService.getSolutions(problemId).stream().map(Solution::toDto).toList();
  }

  @PatchMapping("/solutions/{id}")
  public void update(@PathVariable long id, @RequestBody @Valid EditSolution solutionDto) {
    solutionService.update(id, solutionDto);
  }

  @DeleteMapping("/solutions/{id}")
  public void delete(@PathVariable long id) {
    solutionService.delete(id);
  }
}
