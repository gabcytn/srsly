package me.gabcytn.srsly.Controller;

import jakarta.validation.Valid;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.*;
import me.gabcytn.srsly.DTO.Review.InitialProblemReview;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class SolvedProblemController {
  private final SolvedProblemService solvedProblemService;
  private final AttemptService attemptService;
  private final UserService userService;

  @GetMapping("/review")
  public PaginatedSolvedProblem getTodayProblems(
      @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(name = "difficulty", required = false, defaultValue = "all") String difficulty,
      @RequestParam(name = "title", required = false) String titleSearch) {
    return solvedProblemService.getTodayProblems(page, difficulty, titleSearch);
  }

  @GetMapping("/review/progress")
  public ReviewProgress progress() {
    User user = userService.getCurrentUser();
    Integer solvedTodayCount = attemptService.countSolvedTodayExcludingInitial(user);
    Integer unsolvedCount = solvedProblemService.countOfProblemsToSolveToday();

    return new ReviewProgress(unsolvedCount, solvedTodayCount);
  }

  @PostMapping("/{problemId}/review/initial")
  public ResponseEntity<SolvedProblemDto> saveReview(
      @PathVariable Integer problemId,
      @RequestBody @Valid InitialReviewRequest request,
      @RequestParam(name = "reviewable", defaultValue = "true") Boolean isReviewable) {
    Optional<SolvedProblem> reviewedProblem =
        solvedProblemService.saveInitial(
            InitialProblemReview.builder()
                .initialReviewRequest(request)
                .problemFrontendId(problemId)
                .isReviewable(isReviewable)
                .build());
    if (reviewedProblem.isPresent()) {
      return new ResponseEntity<>(reviewedProblem.get().toDto(), HttpStatus.CREATED);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/review/{id}")
  public ResponseEntity<Void> save(
      @PathVariable int id, @RequestBody @Valid ReviewedProblem reviewedProblem) {
    solvedProblemService.saveSubsequent(id, reviewedProblem.grade());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
