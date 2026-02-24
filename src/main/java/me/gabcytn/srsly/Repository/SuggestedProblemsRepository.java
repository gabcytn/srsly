package me.gabcytn.srsly.Repository;

import java.util.UUID;
import me.gabcytn.srsly.Entity.SuggestedProblems;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestedProblemsRepository extends ListCrudRepository<SuggestedProblems, UUID> {}
