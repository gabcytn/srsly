package me.gabcytn.srsly.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedSrsProblem;
import me.gabcytn.srsly.DTO.ReviewProgress;
import me.gabcytn.srsly.DTO.ReviewedProblem;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Service.AttemptService;
import me.gabcytn.srsly.Service.SrsProblemService;
import me.gabcytn.srsly.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/problems/srs")
public class SrsProblemController {
  private final SrsProblemService srsProblemService;
  private final AttemptService attemptService;
  private final UserService userService;

  @GetMapping
  @JsonView(Views.Summary.class)
  public PaginatedSrsProblem getTodayProblems(
      @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(name = "difficulty", required = false, defaultValue = "all") String difficulty,
      @RequestParam(name = "title", required = false) String titleSearch) {
    return srsProblemService.getTodayProblems(page, difficulty, titleSearch);
  }

  @GetMapping("/progress")
  public ReviewProgress progress() {
    User user = userService.getCurrentUser();
    Integer solvedTodayCount = attemptService.countSolvedTodayExcludingInitial(user);
    Integer unsolvedCount = srsProblemService.countOfProblemsToSolveToday();

    return new ReviewProgress(unsolvedCount, solvedTodayCount);
  }

  @PostMapping("/{id}")
  public ResponseEntity<Void> save(
      @PathVariable int id, @RequestBody @Valid ReviewedProblem reviewedProblem) {
    srsProblemService.saveSubsequent(id, reviewedProblem.grade());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
