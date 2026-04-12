package me.gabcytn.srsly.Entity;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import me.gabcytn.srsly.DTO.Problem.ProblemSummaryDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("suggested")
public class SuggestedProblems {
  @Id private final UUID id;
  public final List<ProblemSummaryDto> problems;

  public SuggestedProblems(UUID id, List<ProblemSummaryDto> problems) {
    this.id = id;
    this.problems = problems;
  }

  @TimeToLive private Long expiresAt = 86400L;
}
