package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Entity.Attempt;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Repository.AttemptRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AttemptService {
  private final AttemptRepository attemptRepository;

  public void save(Attempt attempt) {
    attemptRepository.save(attempt);
  }

  public Integer getCountOfReviewedProblemsToday(User user) {
    return attemptRepository.countByAttemptedAtAndUserAndGradeIsNotNull(LocalDate.now(), user);
  }
}
