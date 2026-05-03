package me.gabcytn.srsly.Service;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.AOP.Annotation.VerifySolutionOwner;
import me.gabcytn.srsly.DTO.EditSolution;
import me.gabcytn.srsly.DTO.SolutionDto;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.AiException;
import me.gabcytn.srsly.Exception.GenericNotFoundException;
import me.gabcytn.srsly.Exception.SolutionException;
import me.gabcytn.srsly.Repository.SolutionRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SolutionService {
  private final SolutionRepository solutionRepository;
  private final ProblemService problemService;
  private final ReviewProblemService reviewProblemService;
  private final UserService userService;
  private final SolvedProblemService solvedProblemService;

  public Solution saveToProblem(SolutionDto solutionDto, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentUser();
    SolvedProblem solvedProblem = solvedProblemService.findByProblemAndUser(problem, user);

    validateSolutionEligibility(problem, user);
    return this.save(solutionDto.toEntity(solvedProblem));
  }

  private void validateSolutionEligibility(Problem problem, User user) {
    if (!reviewProblemService.existsByProblemAndUser(problem, user)) {
      throw new SolutionException("Initial solutions must be provided during initial review.");
    }

    if (solutionRepository.countBySolvedProblem_ProblemAndSolvedProblem_User(problem, user) >= 5) {
      throw new SolutionException("Unable to save more than 5 solutions to a problem.");
    }
  }

  public Solution findById(long id) {
    Optional<Solution> solution = solutionRepository.findById(id);
    if (solution.isPresent()) {
      return solution.get();
    }

    throw new GenericNotFoundException("Solution not found.");
  }

  public Solution save(Solution solution) {
    return solutionRepository.save(solution);
  }

  public List<Solution> getSolutions(int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentUser();

    return solutionRepository.findAllBySolvedProblem_ProblemAndSolvedProblem_User(problem, user);
  }

  public Boolean existsByProblemAndUser(Problem problem, User user) {
    return solutionRepository.existsBySolvedProblem_ProblemAndSolvedProblem_User(problem, user);
  }

  @VerifySolutionOwner
  public void update(long id, EditSolution dto) {
    Solution solution = findById(id);

    validateSolutionEligibilityToUpdate(solution, dto);
    solution.setTitle(dto.title());
    solution.setNote(dto.note());
    solution.setCode(dto.code());

    save(solution);
  }

  private void validateSolutionEligibilityToUpdate(Solution solution, EditSolution editedSolution) {
    if (isAiCritiqueIssued(solution, editedSolution)) {
      throw new AiException("Code cannot be modified once an AI critique has been completed.");
    }
  }

  private boolean isAiCritiqueIssued(Solution solution, EditSolution editedSolution) {
    return solution.getAiCritique() != null && !solution.getCode().equals(editedSolution.code());
  }

  @VerifySolutionOwner
  public void delete(long id) {
    solutionRepository.deleteById(id);
  }
}
