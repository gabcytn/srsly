package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.Entity.Attempt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptRepository extends CrudRepository<Attempt, Long> {}
