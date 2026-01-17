package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "solutions")
public class Solution {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private String aiCritique;

  @Column(nullable = false)
  private String note;

  @ManyToOne
  @JoinColumn(name = "problem_id")
  private Problem problem;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
