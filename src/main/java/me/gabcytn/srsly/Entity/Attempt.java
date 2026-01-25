package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "attempts")
public class Attempt {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(nullable = false)
	private int grade;

	@Column(nullable = false)
	private LocalDate attemptedAt;

	@ManyToOne
	@JoinColumn(name = "problem_id", nullable = false)
	private Problem problem;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
