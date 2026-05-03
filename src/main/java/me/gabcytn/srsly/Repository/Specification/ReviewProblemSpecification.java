package me.gabcytn.srsly.Repository.Specification;

import me.gabcytn.srsly.Entity.ReviewProblem;
import me.gabcytn.srsly.Entity.User;
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
        cb.equal(cb.lower(root.get("solvedProblem").get("problem").get("difficulty")), difficulty.toLowerCase());
  }

  public Specification<ReviewProblem> hasUser(User user) {
    return (root, query, cb) -> cb.equal(root.get("solvedProblem").get("user"), user);
  }
}
