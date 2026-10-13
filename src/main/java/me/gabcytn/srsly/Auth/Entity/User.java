package me.gabcytn.srsly.Auth.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import me.gabcytn.srsly.Auth.DTO.Oauth.SignUpMethod;
import me.gabcytn.srsly.Problem.Entity.SolvedProblem;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(unique = true, nullable = false)
  private String email;

  private String signUpMethod;

  private Boolean isSubscribedToMailReminders = false;

  @JsonIgnore
  @CreationTimestamp
  @Column(updatable = false)
  private Timestamp createdAt;

  @JsonIgnore @UpdateTimestamp private Timestamp updatedAt;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<SolvedProblem> solvedProblems;

  public static User ofEmailAndSignUpMethod(String email, SignUpMethod signUpMethod) {
    User user = new User();
    user.setEmail(email);
    user.setSignUpMethod(signUpMethod.getProvider());
    return user;
  }
}
