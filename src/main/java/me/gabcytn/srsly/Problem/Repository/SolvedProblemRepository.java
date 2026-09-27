package me.gabcytn.srsly.Problem.Repository;

import java.util.Optional;
import me.gabcytn.srsly.Auth.Entity.User;
import me.gabcytn.srsly.Problem.Entity.Problem;
import me.gabcytn.srsly.Problem.Entity.SolvedProblem;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SolvedProblemRepository
    extends JpaRepository<SolvedProblem, Long>, JpaSpecificationExecutor<SolvedProblem> {
  Boolean existsByProblemAndUser(Problem problem, User user);

  Page<SolvedProblem> findAll(Specification<SolvedProblem> spec, Pageable pageable);

  Optional<SolvedProblem> findByProblemAndUser(Problem problem, User user);
}
