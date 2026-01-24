package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedSrsProblem;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SrsProblem;
import me.gabcytn.srsly.Repository.SrsProblemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SrsProblemService {
  private final SrsProblemRepository srsProblemRepository;
  private final UserService userService;

  public void saveInitialSrsProblem(Problem problem, int grade) {
    SrsProblem srsProblem = new SrsProblem();
    srsProblem.setEaseFactor(newEaseFactor(2.5, grade));
    srsProblem.setLastAttemptAt(LocalDate.now());
    srsProblem.setNextAttemptAt(LocalDate.now().plusDays(1));
    srsProblem.setUser(userService.getCurrentlyLoggedInUser());
    srsProblem.setProblem(problem);

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

  private double newEaseFactor(double oldEaseFactor, int grade) {
    return Math.max(oldEaseFactor + (0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02)), 1.3);
  }
}
