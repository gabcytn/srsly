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

  public PaginatedProblemDto(Page<Problem> pagedSrsProblem) {
    List<ProblemSummaryDto> problemDtoList = new ArrayList<>();
    for (Problem problem : pagedSrsProblem.getContent()) {
      problemDtoList.add(problem.summarize());
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
