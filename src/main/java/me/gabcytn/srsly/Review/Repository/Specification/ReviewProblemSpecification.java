package me.gabcytn.srsly.Review.Repository.Specification;

import java.time.LocalDate;
import me.gabcytn.srsly.Auth.Entity.User;
import me.gabcytn.srsly.Problem.Repository.Specification.ProblemSearchSpecification;
import me.gabcytn.srsly.Review.Entity.ReviewProblem;
import org.springframework.data.jpa.domain.Specification;

public class ReviewProblemSpecification implements ProblemSearchSpecification<ReviewProblem> {

  public Specification<ReviewProblem> hasTitle(String title) {
    return (root, query, cb) ->
        cb.like(
            cb.lower(root.get("solvedProblem").get("problem").get("title")),
            "%" + title.toLowerCase() + "%");
  }

  public Specification<ReviewProblem> hasDifficulty(String difficulty) {
    return (root, query, cb) ->
        cb.equal(
            cb.lower(root.get("solvedProblem").get("problem").get("difficulty")),
            difficulty.toLowerCase());
  }

  public Specification<ReviewProblem> hasUser(User user) {
    return (root, query, cb) -> cb.equal(root.get("solvedProblem").get("user"), user);
  }

  public Specification<ReviewProblem> hasNextAttemptAtLessThanOrEqualTo(LocalDate date) {
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("nextAttemptAt"), date);
  }
}
