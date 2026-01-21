package me.gabcytn.srsly.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.DTO.Annotation.IsEaseFactorRequiredCheck;
import me.gabcytn.srsly.Entity.Problem;
import me.gabcytn.srsly.Entity.Solution;
import me.gabcytn.srsly.Entity.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@IsEaseFactorRequiredCheck
public class SolutionDto {
  @NotNull(message = "Code solution is required.")
  private String code;

  @NotNull(message = "Is initial is required.")
  private Boolean isInitial;

  private String aiCritique;
  private String note;
  private Integer easeFactor;

  public Solution toSolutionEntity(Problem problem, User user) {
    Solution s = new Solution();
    s.setCode(code);
    s.setAiCritique(aiCritique);
    s.setNote(note);
    s.setProblem(problem);
    s.setUser(user);
    return s;
  }
}
