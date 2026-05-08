package me.gabcytn.srsly.Controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.EditSolution;
import me.gabcytn.srsly.DTO.SolutionDto;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Service.SolutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Problem Solutions")
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class SolutionController {
  private final SolutionService solutionService;

  @Operation(summary = "Submit solution")
  @PostMapping("/problems/{problemId}/solutions")
  public ResponseEntity<SolutionDto> save(
      @PathVariable int problemId, @RequestBody @Valid SolutionDto solutionDto) {
    Solution s = solutionService.saveToProblem(solutionDto, problemId);
    return new ResponseEntity<>(s.toDto(), HttpStatus.CREATED);
  }

  @Operation(summary = "Get solutions to a problem")
  @GetMapping("/problems/{problemId}/solutions")
  public List<SolutionDto> getSolutions(@PathVariable int problemId) {
    return solutionService.getSolutions(problemId).stream().map(Solution::toDto).toList();
  }

  @Operation(summary = "Update solution")
  @PatchMapping("/solutions/{id}")
  public SolutionDto update(@PathVariable long id, @RequestBody @Valid EditSolution solutionDto) {
    Solution s = solutionService.update(id, solutionDto);
    return s.toDto();
  }

  @Operation(summary = "Delete solution")
  @DeleteMapping("/solutions/{id}")
  public ResponseEntity<Void> delete(@PathVariable long id) {
    solutionService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
