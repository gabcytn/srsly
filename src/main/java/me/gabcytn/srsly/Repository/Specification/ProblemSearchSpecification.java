package me.gabcytn.srsly.Repository.Specification;

import me.gabcytn.srsly.Entity.User;
import org.springframework.data.jpa.domain.Specification;

public interface ProblemSearchSpecification<T> {
  public Specification<T> hasTitle(String title);

  public Specification<T> hasDifficulty(String difficulty);

  public Specification<T> hasUser(User user);
}
