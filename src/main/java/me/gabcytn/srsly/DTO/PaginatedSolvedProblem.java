package me.gabcytn.srsly.DTO;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.gabcytn.srsly.Entity.SolvedProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@Getter
public class PaginatedSolvedProblem {
  private final List<SolvedProblemDto> content;

  private final int page;

  private final int size;

  private final long totalElements;

  private final int totalPages;

  private final int numberOfElements;

  private final Sort sort;

  public PaginatedSolvedProblem(Page<SolvedProblem> pagedSolvedProblem) {
    List<SolvedProblemDto> problemDtoList = new ArrayList<>();
    for (SolvedProblem problem : pagedSolvedProblem.getContent()) {
      problemDtoList.add(problem.toDto());
    }

    this.totalPages = pagedSolvedProblem.getTotalPages();
    this.totalElements = pagedSolvedProblem.getTotalElements();
    this.page = pagedSolvedProblem.getNumber();
    this.numberOfElements = pagedSolvedProblem.getNumberOfElements();
    this.size = pagedSolvedProblem.getSize();
    this.sort = pagedSolvedProblem.getSort();
    this.content = problemDtoList;
  }
}
