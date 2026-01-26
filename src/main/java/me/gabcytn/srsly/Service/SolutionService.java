package me.gabcytn.srsly.Service;

import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.SolutionDto;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Repository.SolutionRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SolutionService {
  private final SolutionRepository solutionRepository;
  private final ProblemService problemService;
  private final SrsProblemService srsProblemService;
  private final UserService userService;

  public void saveToProblem(SolutionDto solutionDto, int problemId) {
    Problem problem = problemService.findByFrontendId(problemId);
    User user = userService.getCurrentlyLoggedInUser();

    if (!solutionRepository.existsByProblemAndUser(problem, user)) {
      srsProblemService.saveInitial(problem);
    }
    solutionRepository.save(solutionDto.toSolutionEntity(problem, user));
  }
}
