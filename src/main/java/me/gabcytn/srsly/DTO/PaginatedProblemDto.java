package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Entity.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PaginatedProblemDto {
	@JsonView(Views.Summary.class)
	private final List<ProblemDto> content;

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

	public PaginatedProblemDto(Page<Problem> pagedSrsProblem) {
		List<ProblemDto> problemDtoList = new ArrayList<>();
		for (Problem problem : pagedSrsProblem.getContent()) {
			problemDtoList.add(problem.toApiPied());
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
