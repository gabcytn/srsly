package me.gabcytn.srsly.Entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.gabcytn.srsly.DTO.ProblemDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
@RedisHash("suggested")
public class SuggestedProblems {
	@Id
	private final UUID id;
	public final List<ProblemDto> problems;
	@TimeToLive
	private Long expiresAt = 86400L;
}
