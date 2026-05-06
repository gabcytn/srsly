package me.gabcytn.srsly.Auth.RateLimiter;

import io.github.bucket4j.Bandwidth;
import java.time.Duration;

public enum EndpointLimit {
  LOGIN("/login", 5, 5, Duration.ofMinutes(1)),
  REGISTER("/register", 3, 3, Duration.ofMinutes(1)),
  REFRESH("/refresh-token", 20, 20, Duration.ofHours(1));

  public final String path;
  private final int capacity;
  private final int refill;
  private final Duration duration;

  EndpointLimit(String path, int capacity, int refill, Duration duration) {
    this.path = path;
    this.capacity = capacity;
    this.refill = refill;
    this.duration = duration;
  }

  public static Bandwidth resolve(String endpoint) {
    for (EndpointLimit limit : values()) {
      if (endpoint.endsWith(limit.path))
        return Bandwidth.builder()
            .capacity(limit.capacity)
            .refillIntervally(limit.refill, limit.duration)
            .build();
    }

    throw new IllegalArgumentException("Unknown endpoint: " + endpoint);
  }
}
