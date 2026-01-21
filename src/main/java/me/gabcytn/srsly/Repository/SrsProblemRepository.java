package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.SrsProblem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SrsProblemRepository extends CrudRepository<SrsProblem, Integer> {}
