package me.gabcytn.srsly.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.*;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class SolvedProblemController {
  private final SolvedProblemService solvedProblemService;
  private final ProblemFacadeService problemFacadeService;

  @GetMapping("/review")
  public PaginatedSolvedProblem getTodayProblems(
      @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
      @RequestParam(name = "difficulty", required = false, defaultValue = "all") String difficulty,
      @RequestParam(name = "title", required = false) String titleSearch) {
    ReviewableProblemsFilter filter =
        ReviewableProblemsFilter.builder()
            .page(page)
            .difficulty(difficulty)
            .title(titleSearch)
            .build();
    return problemFacadeService.getProblemsToReviewToday(filter);
  }

  @GetMapping("/review/progress")
  public ReviewProgress progress() {
    return problemFacadeService.getReviewProgress();
  }

  /** Spaced-repetition review */
  @PostMapping("/{problemId}/review/initial")
  public ResponseEntity<SolvedProblemDto> saveReview(
      @PathVariable Integer problemId, @RequestBody @Valid InitialReviewRequest request) {
    SolvedProblem reviewedProblem =
        problemFacadeService.saveInitialAsReviewable(request, problemId);
    return new ResponseEntity<>(reviewedProblem.toDto(), HttpStatus.CREATED);
  }

  /** No spaced-repetition review */
  @PostMapping("/{problemId}/solve/initial")
  public ResponseEntity<SolvedProblemDto> markProblemAsSolved(@PathVariable Integer problemId) {
    SolvedProblem solvedProblem = problemFacadeService.saveInitialAsNonReviewable(problemId);
    return new ResponseEntity<>(solvedProblem.toDto(), HttpStatus.CREATED);
  }

  @PostMapping("/review/{id}")
  public ResponseEntity<Void> save(
      @PathVariable int id, @RequestBody @Valid ReviewedProblem reviewedProblem) {
    solvedProblemService.saveSubsequent(id, reviewedProblem.grade());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/solved")
  public PaginatedSolvedProblem getSolvedProblems(
      @RequestParam(name = "page", defaultValue = "0") int page) {
    return problemFacadeService.findProblemsSolvedByUser(page);
  }
}
