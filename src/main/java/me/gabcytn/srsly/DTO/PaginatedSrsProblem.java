package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.SrsProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@Getter
public class PaginatedSrsProblem {
  @JsonView(Views.Summary.class)
  private final List<SrsProblemDto> content;

  @JsonView(Views.Summary.class)
  private final int page;

  @JsonView(Views.Summary.class)
  private final int size;

  @JsonView(Views.Summary.class)
  private final long totalElements;

  @JsonView(Views.Summary.class)
  private final int totalPages;

  @JsonView(Views.Summary.class)
  private final int numberOfElements;

  @JsonView(Views.Summary.class)
  private final Sort sort;

  public PaginatedSrsProblem(Page<SrsProblem> pagedSrsProblem) {
    List<SrsProblemDto> problemDtoList = new ArrayList<>();
    for (SrsProblem problem : pagedSrsProblem.getContent()) {
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
