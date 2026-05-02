package me.gabcytn.srsly.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.gabcytn.srsly.DTO.UserProblemToSolveCount;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.ReviewProblem;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewProblemRepository
    extends JpaRepository<ReviewProblem, Integer>, JpaSpecificationExecutor<ReviewProblem> {
  Page<ReviewProblem> findBySolvedProblem_User(User user, Pageable pageable);

  Page<ReviewProblem> findBySolvedProblem_User(
      User user, Specification<ReviewProblem> specs, Pageable pageable);

  @Query(
"""
    SELECT DISTINCT srs1.solvedProblem.problem
    FROM ReviewProblem srs1
    WHERE NOT EXISTS (
        SELECT 1
        FROM ReviewProblem srs2
        WHERE srs2.solvedProblem.problem.id = srs1.solvedProblem.problem.id
          AND srs2.solvedProblem.user.id = :userId
    )
""")
  List<Problem> findProblemsNotSolvedByUser(@Param("userId") UUID userId);

  Boolean existsBySolvedProblem_ProblemAndSolvedProblem_User(Problem problem, User user);

  Optional<ReviewProblem> findBySolvedProblem_ProblemAndSolvedProblem_User(
      Problem problem, User user);

  Integer countByNextAttemptAtLessThanEqualAndSolvedProblem_User(LocalDate date, User user);

  @Query(
"""
    SELECT u.email, COUNT(srs.id) FROM ReviewProblem srs
    JOIN srs.solvedProblem.user u WHERE srs.nextAttemptAt <= :date AND u.emailVerifiedAt IS NOT NULL GROUP BY u.email
""")
  List<UserProblemToSolveCount> findUserWithToSolveCountByNextAttemptAtLessThanEqual(
      @Param("date") LocalDate nextAttemptAt);
}
