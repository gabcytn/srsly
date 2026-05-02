package me.gabcytn.srsly.Repository.Specification;

import me.gabcytn.srsly.Entity.ReviewProblem;
import org.springframework.data.jpa.domain.Specification;

public class ReviewProblemSpecification {

  public static Specification<ReviewProblem> hasTitle(String title) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  public static Specification<ReviewProblem> hasDifficulty(String difficulty) {
    return (root, query, cb) -> cb.equal(root.get("difficulty"), difficulty);
  }
}
