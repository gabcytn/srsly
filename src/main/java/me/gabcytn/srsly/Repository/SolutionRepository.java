package me.gabcytn.srsly.Repository;

import java.util.List;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends ListCrudRepository<Solution, Long> {
  Boolean existsBySolvedProblem_ProblemAndSolvedProblem_User(Problem problem, User user);

  Integer countBySolvedProblem_ProblemAndSolvedProblem_User(Problem problem, User user);

  List<Solution> findAllBySolvedProblem_ProblemAndSolvedProblem_User(Problem problem, User user);

  void deleteById(Long id);
}
