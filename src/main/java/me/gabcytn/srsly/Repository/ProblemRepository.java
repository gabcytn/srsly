package me.gabcytn.srsly.Repository;

import java.util.Optional;
import java.util.UUID;
import me.gabcytn.srsly.Entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Integer> {
  Optional<Problem> findByFrontendId(int frontendId);

  Page<Problem> findBySolvers_Id(UUID solversId, Pageable pageable);
}
