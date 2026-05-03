package me.gabcytn.srsly.Repository.Specification;

import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.jpa.domain.Specification;

public class SolvedProblemSpecification implements ProblemSearchSpecification<SolvedProblem> {

  public Specification<SolvedProblem> hasTitle(String title) {
    return (root, query, cb) ->
        cb.like(cb.lower(root.get("problem").get("title")), "%" + title.toLowerCase() + "%");
  }

  public Specification<SolvedProblem> hasDifficulty(String difficulty) {
    return (root, query, cb) ->
        cb.equal(cb.lower(root.get("problem").get("difficulty")), difficulty.toLowerCase());
  }

  public Specification<SolvedProblem> hasUser(User user) {
    return (root, query, cb) -> cb.equal(root.get("user"), user);
  }
}
