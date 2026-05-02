package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblem, Long> {
  Boolean existsByProblemAndUser(Problem problem, User user);

  Page<SolvedProblem> findByUser(User user, Pageable pageable);
}
