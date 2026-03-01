package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.DTO.View.Views;
import me.gabcytn.srsly.Model.ProblemStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SrsProblemDto {
  @JsonView(Views.Summary.class)
  private int id;

  @JsonView(Views.Detailed.class)
  private double grade;

  @JsonView(Views.Summary.class)
  private int repetitions;

  @JsonView(Views.Summary.class)
  private LocalDate lastAttemptAt;

  @JsonView(Views.Summary.class)
  private LocalDate nextAttemptAt;

  @JsonView(Views.Summary.class)
  private ProblemStatus status;

  @JsonView(Views.Summary.class)
  private ProblemDto problem;
}
