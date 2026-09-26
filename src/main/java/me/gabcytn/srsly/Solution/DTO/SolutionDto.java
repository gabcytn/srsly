package me.gabcytn.srsly.Solution.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.AI.DTO.AiCritique;
import me.gabcytn.srsly.Solution.Entity.Solution;
import me.gabcytn.srsly.Problem.Entity.SolvedProblem;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionDto {
  private Long id;

  @NotNull(message = "Code solution is required.")
  @NotBlank(message = "Title must not be blank.")
  private String code;

  @NotNull(message = "Title is required.")
  @NotBlank(message = "Title must not be blank.")
  private String title;

  private AiCritique aiCritique;
  private String note;

  public Solution toEntity(SolvedProblem solvedProblem) {
    Solution s = new Solution();
    s.setCode(code);
    s.setTitle(title);
    s.setAiCritique(aiCritique);
    s.setNote(note);
    s.setSolvedProblem(solvedProblem);
    return s;
  }
}
