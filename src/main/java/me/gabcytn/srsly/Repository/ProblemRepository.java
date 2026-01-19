package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.Problem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemRepository extends CrudRepository<Problem, Integer> {}
