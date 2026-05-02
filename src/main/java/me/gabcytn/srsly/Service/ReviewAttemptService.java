package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Entity.ReviewAttempt;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Repository.ReviewAttemptRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReviewAttemptService
{
  private final ReviewAttemptRepository reviewAttemptRepository;

  public void save(ReviewAttempt reviewAttempt) {
    reviewAttemptRepository.save(reviewAttempt);
  }

  public Integer getCountOfReviewedProblemsToday(User user) {
    return reviewAttemptRepository.countByAttemptedAtAndSolvedProblem_UserAndGradeIsNotNull(LocalDate.now(), user);
  }
}
