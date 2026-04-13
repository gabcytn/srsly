package me.gabcytn.srsly.DTO;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.gabcytn.srsly.Entity.SolvedProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@Getter
public class PaginatedSolvedProblem
{
  private final List<SrsProblemDto> content;

  private final int page;

  private final int size;

  private final long totalElements;

  private final int totalPages;

  private final int numberOfElements;

  private final Sort sort;

  public PaginatedSolvedProblem(Page<SolvedProblem> pagedSrsProblem) {
    List<SrsProblemDto> problemDtoList = new ArrayList<>();
    for (SolvedProblem problem : pagedSrsProblem.getContent()) {
      problemDtoList.add(problem.toDto());
    }

    this.totalPages = pagedSrsProblem.getTotalPages();
    this.totalElements = pagedSrsProblem.getTotalElements();
    this.page = pagedSrsProblem.getNumber();
    this.numberOfElements = pagedSrsProblem.getNumberOfElements();
    this.size = pagedSrsProblem.getSize();
    this.sort = pagedSrsProblem.getSort();
    this.content = problemDtoList;
  }
}
