package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @ManyToMany(mappedBy = "tags")
  private Set<Problem> problems;

  public Tag(String name) {
    this.name = name;
  }
}
