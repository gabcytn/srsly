package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.AI.AiCritique;
import me.gabcytn.srsly.DTO.SolutionDto;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "solutions",
    indexes = {
      @Index(name = "idx_problem", columnList = "problem_id"),
      @Index(name = "idx_user", columnList = "user_id")
    })
public class Solution {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String code;

  @JdbcTypeCode(SqlTypes.JSON)
  private AiCritique aiCritique;

  @Column(columnDefinition = "TEXT")
  private String note;

  @ManyToOne
  @JoinColumn(name = "problem_id", nullable = false)
  private Problem problem;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public SolutionDto toDto() {
    return new SolutionDto(code, title, aiCritique, note);
  }
}
