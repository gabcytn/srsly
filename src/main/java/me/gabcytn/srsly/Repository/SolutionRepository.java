package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends CrudRepository<Solution, Integer> {
	Boolean existsByProblemAndUser(Problem problem, User user);
	Integer countByProblemAndUser(Problem problem, User user);
}
