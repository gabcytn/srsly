package me.gabcytn.srsly.AI.Repository;

import me.gabcytn.srsly.AI.DTO.AiCritiqueLimit;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiCritiqueLimitRepository extends ListCrudRepository<AiCritiqueLimit, String> {
  Boolean existsByIdAndUsageCountLessThan(String id, Integer count);
}
