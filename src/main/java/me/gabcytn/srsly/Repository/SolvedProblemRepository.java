package me.gabcytn.srsly.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.gabcytn.srsly.DTO.UserProblemToSolveCount;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.DTO.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SolvedProblemRepository extends JpaRepository<SolvedProblem, Integer> {
  Page<SolvedProblem> findByUserAndNextAttemptAtLessThanEqual(
      User user, LocalDate date, Pageable pageable);

  Page<SolvedProblem>
      findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCaseAndProblem_Difficulty(
          User user, LocalDate date, String titleSearch, Difficulty difficulty, Pageable pageable);

  Page<SolvedProblem> findByUserAndNextAttemptAtLessThanEqualAndProblem_TitleContainingIgnoreCase(
      User user, LocalDate date, String titleSearch, Pageable pageable);

  Page<SolvedProblem> findByUserAndNextAttemptAtLessThanEqualAndProblem_Difficulty(
      User user, LocalDate date, Difficulty difficulty, Pageable pageable);

  Page<SolvedProblem> findByUser(User user, Pageable pageable);

  @Query(
"""
    SELECT DISTINCT srs1.problem
    FROM SolvedProblem srs1
    WHERE NOT EXISTS (
        SELECT 1
        FROM SolvedProblem srs2
        WHERE srs2.problem.id = srs1.problem.id
          AND srs2.user.id = :userId
    )
""")
  List<Problem> findProblemsNotSolvedByUser(@Param("userId") UUID userId);

  Boolean existsByProblemAndUser(Problem problem, User user);

  Optional<SolvedProblem> findByProblemAndUser(Problem problem, User user);

  Integer countByNextAttemptAtLessThanEqualAndUser(LocalDate date, User user);

  @Query(
"""
    SELECT u.email, COUNT(srs.id) FROM SolvedProblem srs
    JOIN srs.user u WHERE srs.nextAttemptAt <= :date AND u.emailVerifiedAt IS NOT NULL GROUP BY u.email
""")
  List<UserProblemToSolveCount> findUserWithToSolveCountByNextAttemptAtLessThanEqual(
      @Param("date") LocalDate nextAttemptAt);
}
