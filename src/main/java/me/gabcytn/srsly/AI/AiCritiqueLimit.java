package me.gabcytn.srsly.AI;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "aiCritiqueLimit", timeToLive = 86400)
public class AiCritiqueLimit {
  @Id private final String id;
  private Integer usageCount;
  private final LocalDateTime startTime;

  public AiCritiqueLimit(String id, Integer usageCount, LocalDateTime startTime) {
    this.id = id;
    this.usageCount = usageCount;
    this.startTime = startTime;
  }

  public static AiCritiqueLimit ofInitial(String id) {
    return new AiCritiqueLimit(id, 1, LocalDateTime.now());
  }

  public void incrementUsage() {
    usageCount++;
  }

  public LocalDateTime getResetTime() {
    return startTime.plusDays(1);
  }
}
