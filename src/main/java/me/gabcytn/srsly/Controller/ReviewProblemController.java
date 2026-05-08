package me.gabcytn.srsly.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.*;
import me.gabcytn.srsly.DTO.Review.InitialReviewRequest;
import me.gabcytn.srsly.Entity.ReviewProblem;
import me.gabcytn.srsly.Service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Spaced Repetition Review")
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ReviewProblemController {
  private final ReviewProblemService reviewProblemService;
  private final ProblemFacadeService problemFacadeService;

  @Operation(summary = "Problems to review today")
  @GetMapping("/review")
  public PaginatedReviewProblem getTodayProblems(
      @RequestParam(name = "page", defaultValue = "0") Integer page,
      @RequestParam(name = "difficulty", defaultValue = "all") String difficulty,
      @RequestParam(name = "title", defaultValue = "") String titleSearch) {
    ProblemSearchFilter filter =
        ProblemSearchFilter.builder().page(page).difficulty(difficulty).title(titleSearch).build();
    return problemFacadeService.getProblemsToReviewToday(filter);
  }

  @Operation(summary = "Today's progress")
  @GetMapping("/review/progress")
  public ReviewProgress progress() {
    return problemFacadeService.getReviewProgress();
  }

  @Operation(summary = "Start reviewing a problem")
  @PostMapping("/{problemId}/review/initial")
  public ResponseEntity<ReviewProblemDto> saveReview(
      @PathVariable Integer problemId, @RequestBody @Valid InitialReviewRequest request) {
    ReviewProblem reviewedProblem =
        problemFacadeService.saveInitialAsReviewable(request, problemId);
    return new ResponseEntity<>(reviewedProblem.toDto(), HttpStatus.CREATED);
  }

  @Operation(summary = "Mark problem as solved")
  @PostMapping("/{problemId}/solve/initial")
  public ResponseEntity<Void> markProblemAsSolved(@PathVariable Integer problemId) {
    problemFacadeService.saveInitialAsNonReviewable(problemId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Review a problem")
  @PostMapping("/review/{id}")
  public ResponseEntity<Void> save(
      @PathVariable int id, @RequestBody @Valid ReviewedProblem reviewedProblem) {
    reviewProblemService.saveSubsequent(id, reviewedProblem.grade());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(summary = "All solved problems")
  @GetMapping("/solved")
  public PaginatedSolvedProblem getSolvedProblems(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "difficulty", defaultValue = "all") String difficulty,
      @RequestParam(name = "title", defaultValue = "") String titleSearch) {
    ProblemSearchFilter filter =
        ProblemSearchFilter.builder().page(page).difficulty(difficulty).title(titleSearch).build();
    return problemFacadeService.findProblemsSolvedByUser(filter);
  }
}
