package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolvedProblemRepository extends ListCrudRepository<SolvedProblem, Long> {
  Boolean existsByProblemAndUser(Problem problem, User user);
}
