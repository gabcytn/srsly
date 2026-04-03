package me.gabcytn.srsly.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.*;
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

  private String password;

  private LocalDateTime emailVerifiedAt;

  private Boolean isSubscribedToMailReminders = false;

  @JsonIgnore
  @CreationTimestamp
  @Column(updatable = false)
  private Timestamp createdAt;

  @JsonIgnore @UpdateTimestamp private Timestamp updatedAt;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<SrsProblem> srsProblems;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<Solution> solutions;

  public static User ofEmailAndPassword(String email, String password) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(password);
    return user;
  }
}
