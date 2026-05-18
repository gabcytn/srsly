package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedSolvedProblem;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.SolvedProblem;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.GenericNotFoundException;
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

  public PaginatedSolvedProblem findByUser(
      Specification<SolvedProblem> spec, User user, int pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("createdAt").descending());
    Page<SolvedProblem> data = repository.findAll(spec, pageable);

    return new PaginatedSolvedProblem(data);
  }

  public SolvedProblem findByProblemAndUser(Problem problem, User user) {
    Optional<SolvedProblem> sOptional = repository.findByProblemAndUser(problem, user);
    if (sOptional.isPresent()) {
      return sOptional.get();
    }

    throw new GenericNotFoundException("SolvedProblem not found.");
  }
}
