package me.gabcytn.srsly.Repository;

import java.time.LocalDate;
import java.util.List;
import me.gabcytn.srsly.Entity.Attempt;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptRepository extends ListCrudRepository<Attempt, Long> {
  Integer countByAttemptedAtAndUserAndGradeIsNotNull(LocalDate attemptedAt, User user);
}
