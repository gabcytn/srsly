package me.gabcytn.srsly.Service;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedSolvedProblem;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Repository.SolvedProblemRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SolvedProblemService {
  private final SolvedProblemRepository repository;

  public Boolean existsByProblemAndUser(Problem problem, User user) {
    return repository.existsByProblemAndUser(problem, user);
  }

  public SolvedProblem save(SolvedProblem solvedProblem) {
    return repository.save(solvedProblem);
  }

  public PaginatedSolvedProblem findByUser(Specification<SolvedProblem> spec, User user, int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, 10);
    Page<SolvedProblem> data = repository.findAll(spec, pageable);

    return new PaginatedSolvedProblem(data);
  }
}
