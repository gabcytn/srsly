package me.gabcytn.srsly.Review.Service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Review.Entity.ReviewAttempt;
import me.gabcytn.srsly.Auth.Entity.User;
import me.gabcytn.srsly.Review.Repository.ReviewAttemptRepository;
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
