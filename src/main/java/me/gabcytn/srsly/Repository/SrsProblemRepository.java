package me.gabcytn.srsly.Repository;

import java.time.LocalDate;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Model.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SrsProblemRepository extends JpaRepository<SrsProblem, Integer> {
  Page<SrsProblem> findByUserAndNextAttemptAtLessThanEqual(
      User user, LocalDate date, Pageable pageable);

  Page<SrsProblem>
      findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCaseAndProblem_Difficulty(
          User user, LocalDate date, String titleSearch, Difficulty difficulty, Pageable pageable);

  Page<SrsProblem> findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCase(
      User user, LocalDate date, String titleSearch, Pageable pageable);

  Page<SrsProblem> findByUserAndNextAttemptAtLessThanEqualAndProblem_Difficulty(
      User user, LocalDate date, Difficulty difficulty, Pageable pageable);

  Boolean existsByProblemAndUser(Problem problem, User user);
}
