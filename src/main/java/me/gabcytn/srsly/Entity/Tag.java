package me.gabcytn.srsly.Entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(nullable = false)
	private String name;

	@ManyToMany(mappedBy = "tags")
	private Set<Problem> problems;
}
