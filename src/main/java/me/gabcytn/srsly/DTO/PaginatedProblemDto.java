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

  public PaginatedProblemDto(Page<Problem> pagedProblem) {
    List<ProblemSummaryDto> problemDtoList = new ArrayList<>();
    for (Problem problem : pagedProblem.getContent()) {
      problemDtoList.add(problem.summarize());
    }

    this.totalPages = pagedProblem.getTotalPages();
    this.totalElements = pagedProblem.getTotalElements();
    this.page = pagedProblem.getNumber();
    this.numberOfElements = pagedProblem.getNumberOfElements();
    this.size = pagedProblem.getSize();
    this.sort = pagedProblem.getSort();
    this.content = problemDtoList;
  }
}
