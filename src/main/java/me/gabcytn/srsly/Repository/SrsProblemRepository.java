package me.gabcytn.srsly.Repository;

import java.time.LocalDate;

import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SrsProblemRepository extends JpaRepository<SrsProblem, Integer> {
  Page<SrsProblem> findByUserAndNextAttemptAt(
      User user, LocalDate date, Pageable pageable);
  Boolean existsByProblemAndUser(Problem problem, User user);
}
