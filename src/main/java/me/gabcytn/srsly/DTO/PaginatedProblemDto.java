package me.gabcytn.srsly.DTO;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;
import me.gabcytn.srsly.Entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@Getter
public class PaginatedProblemDto {
  private final List<ProblemSummaryDto> content;

  private final int page;

  private final int size;

  private final long totalElements;

  private final int totalPages;

  private final int numberOfElements;

  private final Sort sort;

  public PaginatedProblemDto(Page<Problem> pagedSolvedProblem) {
    List<ProblemSummaryDto> problemDtoList = new ArrayList<>();
    for (Problem problem : pagedSolvedProblem.getContent()) {
      problemDtoList.add(problem.summarize());
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
