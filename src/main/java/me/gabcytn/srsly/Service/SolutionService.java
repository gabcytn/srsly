package me.gabcytn.srsly.Service;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.EditSolution;
import me.gabcytn.srsly.DTO.SolutionDto;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.AiException;
import me.gabcytn.srsly.Exception.GenericForbiddenException;
import me.gabcytn.srsly.Exception.GenericNotFoundException;
import me.gabcytn.srsly.Exception.SolutionException;
import me.gabcytn.srsly.Repository.SolutionRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SolutionService {
  private final SolutionRepository solutionRepository;
  private final ProblemService problemService;
  private final SolvedProblemService solvedProblemService;
  private final UserService userService;

  public Solution saveToProblem(SolutionDto solutionDto, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentUser();

    validateSolutionEligibility(problem, user);
    return this.save(solutionDto.toEntity(problem, user));
  }

  private void validateSolutionEligibility(Problem problem, User user) {
    if (!solvedProblemService.existsByProblemAndUser(problem, user)) {
      throw new SolutionException("Initial solutions must be provided during initial review.");
    }

    if (solutionRepository.countByProblemAndUser(problem, user) >= 5) {
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

    return solutionRepository.findAllByProblemAndUser(problem, user);
  }

  public Boolean existsByProblemAndUser(Problem problem, User user) {
    return solutionRepository.existsByProblemAndUser(problem, user);
  }

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

    if (!isCurrentUserAllowedAccessToSolution(solution)) {
      throw new GenericForbiddenException("Access denied to solution.");
    }
  }

  private boolean isAiCritiqueIssued(Solution solution, EditSolution editedSolution) {
    return solution.getAiCritique() != null && !solution.getCode().equals(editedSolution.code());
  }

  private boolean isCurrentUserAllowedAccessToSolution(Solution solution) {
    User user = userService.getCurrentUser();
    return solution.getUser().getId().equals(user.getId());
  }

  public void delete(long id) {
    User user = userService.getCurrentUser();
    Solution solution = findById(id);
    if (!solution.getUser().getId().equals(user.getId())) {
      throw new GenericForbiddenException("Access denied to solution.");
    }
    solutionRepository.deleteById(id);
  }
}
