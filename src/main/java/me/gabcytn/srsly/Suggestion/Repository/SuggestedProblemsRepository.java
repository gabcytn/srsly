package me.gabcytn.srsly.Suggestion.Repository;

import java.util.UUID;
import me.gabcytn.srsly.Suggestion.Entity.SuggestedProblems;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestedProblemsRepository extends ListCrudRepository<SuggestedProblems, UUID> {}
