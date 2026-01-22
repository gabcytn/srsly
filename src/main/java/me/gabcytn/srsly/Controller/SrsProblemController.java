package me.gabcytn.srsly.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.PaginatedSrsProblem;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Service.SrsProblemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/problems/srs")
public class SrsProblemController {
  private final SrsProblemService srsProblemService;

  @GetMapping
  @JsonView(Views.Summary.class)
  public PaginatedSrsProblem getTodayProblems(
      @RequestParam(name = "page", required = false, defaultValue = "0") Integer page) {
    return srsProblemService.getTodayProblems(page);
  }
}
