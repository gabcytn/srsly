package me.gabcytn.srsly.Repository;

import java.util.List;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends ListCrudRepository<Solution, Long> {
  Boolean existsByProblemAndUser(Problem problem, User user);

  Integer countByProblemAndUser(Problem problem, User user);

  List<Solution> findAllByProblemAndUser(Problem problem, User user);

  void deleteById(Long id);
}
