package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedSrsProblem;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Model.ProblemStatus;
import me.gabcytn.srsly.Repository.SrsProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SrsProblemService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SrsProblemService.class);
  private final SrsProblemRepository srsProblemRepository;
  private final UserService userService;

  public void saveInitial(Problem problem) {
    SrsProblem srsProblem = new SrsProblem();
    srsProblem.setEaseFactor(2.5);
    srsProblem.setLastAttemptAt(LocalDate.now());
    srsProblem.setNextAttemptAt(LocalDate.now().plusDays(1));
    srsProblem.setUser(userService.getCurrentlyLoggedInUser());
    srsProblem.setProblem(problem);

    this.save(srsProblem);
  }

  public void saveSubsequent(int id, int grade) {
    Optional<SrsProblem> optionalSrsProblem = srsProblemRepository.findById(id);
    if (optionalSrsProblem.isEmpty()) {
      LOGGER.error("Invalid SRS. Problem has not been attempted before.");
      throw new RuntimeException(); // TODO: create custom exception
    }

    SrsProblem srsProblem = optionalSrsProblem.get();
    if (grade < 3) {
      this.reviewFailed(srsProblem);
      return;
    }

    double updatedEaseFactor = calculateEaseFactor(srsProblem.getEaseFactor(), grade);
    srsProblem.setEaseFactor(updatedEaseFactor);
    srsProblem.setRepetitions(srsProblem.getRepetitions() + 1);

    int repetitions = srsProblem.getRepetitions();
    int interval = srsProblem.getInterval();

    if (repetitions == 1) {
      interval = 1;
    } else if (repetitions == 2) {
      interval = 6;
    } else {
      interval *= (int) updatedEaseFactor;
    }

    if (interval >= 60 && repetitions >= 4) {
      srsProblem.setStatus(ProblemStatus.MASTERED);
    } else if (repetitions > 2) {
      srsProblem.setStatus(ProblemStatus.REVIEWING);
    } else {
      srsProblem.setStatus(ProblemStatus.LEARNING);
    }

    LocalDate now = LocalDate.now();
    srsProblem.setLastAttemptAt(now);
    srsProblem.setNextAttemptAt(now.plusDays(interval));
    srsProblem.setInterval(interval);

    this.save(srsProblem);
  }

  private void reviewFailed(SrsProblem srsProblem) {
    srsProblem.setRepetitions(0);
    srsProblem.setEaseFactor(Math.max(srsProblem.getEaseFactor() - 0.2, 1.3));
    srsProblem.setInterval(1);
    srsProblem.setStatus(ProblemStatus.LEARNING);
    srsProblem.setNextAttemptAt(LocalDate.now().plusDays(1));
    this.save(srsProblem);
  }

  public void save(SrsProblem srsProblem) {
    srsProblemRepository.save(srsProblem);
  }

  public PaginatedSrsProblem getTodayProblems(int page) {
    Pageable pageable = PageRequest.of(page, 10);
    Page<SrsProblem> paginatedSrsProblems =
        srsProblemRepository.findByUserAndNextAttemptAt(
            userService.getCurrentlyLoggedInUser(), LocalDate.now(), pageable);
    return new PaginatedSrsProblem(paginatedSrsProblems);
  }

  private double calculateEaseFactor(double oldEaseFactor, int grade) {
    return Math.max(oldEaseFactor + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02)), 1.3);
  }
}
