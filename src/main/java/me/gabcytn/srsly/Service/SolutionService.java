package me.gabcytn.srsly.Service;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.InitialSolutionDto;
import me.gabcytn.srsly.DTO.SolutionDto;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.GenericNotFoundException;
import me.gabcytn.srsly.Exception.SolutionException;
import me.gabcytn.srsly.Repository.SolutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class SolutionService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SolutionService.class);
  private final SolutionRepository solutionRepository;
  private final ProblemService problemService;
  private final SrsProblemService srsProblemService;
  private final UserService userService;

  public void saveSubsequentToProblem(SolutionDto solutionDto, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentlyLoggedInUser();

    if (!solutionRepository.existsByProblemAndUser(problem, user)) {
      throw new SolutionException(
          "Unable to save initial solution. User must hit 'POST /solutions/initial' first");
    }
    if (solutionRepository.countByProblemAndUser(problem, user) >= 5) {
      throw new SolutionException("Unable to save more than 5 solutions to a problem");
    }
    this.save(solutionDto.toSolutionEntity(problem, user));
  }

  @Transactional
  public void saveInitialToProblem(InitialSolutionDto initialSolutionDto, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentlyLoggedInUser();

    if (solutionRepository.existsByProblemAndUser(problem, user)) {
      throw new SolutionException(
          "Unable to save non-initial solution. User must hit 'POST /solutions' for already solved problems.");
    }
    srsProblemService.saveInitial(initialSolutionDto, problem, user);
    this.save(initialSolutionDto.solutionDto().toSolutionEntity(problem, user));
  }

  public Solution findById(int id) {
    Optional<Solution> solution = solutionRepository.findById(id);
    if (solution.isPresent()) {
      return solution.get();
    }

    throw new GenericNotFoundException("Solution not found.");
  }

  public void save(Solution solution) {
    solutionRepository.save(solution);
  }

  public List<Solution> getSolutions(int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentlyLoggedInUser();

    return solutionRepository.findAllByProblemAndUser(problem, user);
  }
}
