package me.gabcytn.srsly.Review.Repository;

import java.time.LocalDate;

import me.gabcytn.srsly.Review.Entity.ReviewAttempt;
import me.gabcytn.srsly.Auth.Entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewAttemptRepository extends ListCrudRepository<ReviewAttempt, Long> {
  Integer countByAttemptedAtAndSolvedProblem_UserAndGradeIsNotNull(LocalDate attemptedAt, User user);
}
