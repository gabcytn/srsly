package me.gabcytn.srsly.Problem.Repository;

import java.util.Optional;
import me.gabcytn.srsly.Problem.Entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Integer> {
  Optional<Problem> findByFrontendId(int frontendId);
}
