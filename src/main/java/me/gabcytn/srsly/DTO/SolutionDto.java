package me.gabcytn.srsly.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.AI.AiCritique;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.User;

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

  public Solution toSolutionEntity(Problem problem, User user) {
    Solution s = new Solution();
    s.setCode(code);
    s.setTitle(title);
    s.setAiCritique(aiCritique);
    s.setNote(note);
    s.setProblem(problem);
    s.setUser(user);
    return s;
  }
}
