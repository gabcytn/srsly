package me.gabcytn.srsly.Service;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.EditSolution;
import me.gabcytn.srsly.DTO.InitialSolutionDto;
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
  private final SrsProblemService srsProblemService;
  private final UserService userService;

  public Solution saveToProblem(SolutionDto solutionDto, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentUser();

    if (!srsProblemService.existsByProblemAndUser(problem, user)) {
      throw new SolutionException("Initial solutions must hit 'POST /solutions/initial' first");
    } else if (solutionRepository.countByProblemAndUser(problem, user) >= 5) {
      throw new SolutionException("Unable to save more than 5 solutions to a problem");
    }
    return this.save(solutionDto.toEntity(problem, user));
  }

  public void saveInitialToProblem(InitialSolutionDto initialSolutionDto, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentUser();

    if (this.existsByProblemAndUser(problem, user)) {
      throw new SolutionException(
          "Unable to save non-initial solution. User must hit 'POST /solutions' for already solved"
              + " problems.");
    }
    srsProblemService.saveInitial(initialSolutionDto, problem, user);
    if (initialSolutionDto.solutionDto() != null)
      this.save(initialSolutionDto.solutionDto().toEntity(problem, user));
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
    User user = userService.getCurrentUser();
    Solution solution = findById(id);
    if (solution.getAiCritique() != null && !solution.getCode().equals(dto.code())) {
      throw new AiException("Code cannot be modified once an AI critique has been completed.");
    } else if (!solution.getUser().getId().equals(user.getId())) {
      throw new GenericForbiddenException("Access denied to solution.");
    }

    solution.setTitle(dto.title());
    solution.setNote(dto.note());
    solution.setCode(dto.code());

    save(solution);
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
