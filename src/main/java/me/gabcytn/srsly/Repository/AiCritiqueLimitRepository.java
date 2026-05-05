package me.gabcytn.srsly.Repository;

import me.gabcytn.srsly.AI.AiCritiqueLimit;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiCritiqueLimitRepository extends ListCrudRepository<AiCritiqueLimit, String> {
  Boolean existsByIdAndUsageCountLessThan(String id, Integer count);
}
