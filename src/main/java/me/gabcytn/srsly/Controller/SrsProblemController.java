package me.gabcytn.srsly.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.*;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class SrsProblemController {
  private final SrsProblemService srsProblemService;
  private final AttemptService attemptService;
  private final UserService userService;

  @GetMapping("/srs")
  public PaginatedSrsProblem getTodayProblems(
      @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(name = "difficulty", required = false, defaultValue = "all") String difficulty,
      @RequestParam(name = "title", required = false) String titleSearch) {
    return srsProblemService.getTodayProblems(page, difficulty, titleSearch);
  }

  @GetMapping("/srs/progress")
  public ReviewProgress progress() {
    User user = userService.getCurrentUser();
    Integer solvedTodayCount = attemptService.countSolvedTodayExcludingInitial(user);
    Integer unsolvedCount = srsProblemService.countOfProblemsToSolveToday();

    return new ReviewProgress(unsolvedCount, solvedTodayCount);
  }

  @PostMapping("/{problemId}/srs/initial")
  public ResponseEntity<Void> saveReview(
      @PathVariable Integer problemId,
      @RequestBody @Valid InitialReviewRequest request,
      @RequestParam(name = "reviewable", defaultValue = "true") Boolean isReviewable) {
    srsProblemService.saveInitial(request, problemId);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/srs/{id}")
  public ResponseEntity<Void> save(
      @PathVariable int id, @RequestBody @Valid ReviewedProblem reviewedProblem) {
    srsProblemService.saveSubsequent(id, reviewedProblem.grade());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
