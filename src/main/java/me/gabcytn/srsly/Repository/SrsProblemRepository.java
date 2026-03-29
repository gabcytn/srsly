package me.gabcytn.srsly.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.gabcytn.srsly.DTO.UserProblemToSolveCount;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Model.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  @Query(
"""
    SELECT DISTINCT srs1.problem
    FROM SrsProblem srs1
    WHERE NOT EXISTS (
        SELECT 1
        FROM SrsProblem srs2
        WHERE srs2.problem.id = srs1.problem.id
          AND srs2.user.id = :userId
    )
""")
  List<Problem> findProblemsNotSolvedByUser(@Param("userId") UUID userId);

  Boolean existsByProblemAndUser(Problem problem, User user);

  Optional<SrsProblem> findByProblemAndUser(Problem problem, User user);

  Integer countByNextAttemptAtLessThanEqualAndUser(LocalDate date, User user);

  @Query(
"""
    SELECT u.email, COUNT(srs.id) FROM SrsProblem srs
    JOIN srs.user u WHERE srs.nextAttemptAt <= :date AND u.isEmailVerified = true GROUP BY u.email
""")
  List<UserProblemToSolveCount> findUserWithToSolveCountByNextAttemptAtLessThanEqual(
      @Param("date") LocalDate nextAttemptAt);
}
