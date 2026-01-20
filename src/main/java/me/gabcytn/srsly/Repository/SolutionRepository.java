package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.Solution;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends CrudRepository<Solution, Integer> {}
