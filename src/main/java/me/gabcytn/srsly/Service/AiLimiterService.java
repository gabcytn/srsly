package me.gabcytn.srsly.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.AI.AiCritiqueLimit;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.GenericNotFoundException;
import me.gabcytn.srsly.Repository.AiCritiqueLimitRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiLimiterService {
  private static final int MAX_AI_USAGE = 3;
  private final AiCritiqueLimitRepository repository;

  public boolean hasUsageLeft(User user) {
    String key = generateKeyFromUser(user);
    return repository.existsByIdAndUsageCountLessThan(key, MAX_AI_USAGE)
        || !repository.existsById(key);
  }

  public void incrementUsage(User user) {
    String key = generateKeyFromUser(user);
    Optional<AiCritiqueLimit> limit = repository.findById(key);

    if (limit.isEmpty()) {
      repository.save(AiCritiqueLimit.ofInitial(key));
      return;
    }

    AiCritiqueLimit limiter = limit.get();
    limiter.incrementUsage();
    repository.save(limiter);
  }

  public LocalDateTime getResetTime(User user) {
    String key = generateKeyFromUser(user);
    Optional<AiCritiqueLimit> limit = repository.findById(key);

    if (limit.isEmpty()) {
      log.error("AI Critique key: {} -> NOT FOUND", key);
      throw new GenericNotFoundException("AI Critique key not found.");
    }

    return limit.get().getResetTime();
  }

  private String generateKeyFromUser(User user) {
    return String.format("user:%s", user.getId());
  }
}
