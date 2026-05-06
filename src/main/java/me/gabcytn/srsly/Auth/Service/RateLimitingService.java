package me.gabcytn.srsly.Auth.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RateLimitingService {
  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  public Bucket findOrCreateBucketByIp(String ip) {
    return cache.computeIfAbsent(ip, this::newBucket);
  }

  private Bucket newBucket(String ip) {
    log.info("Creating bucket for IP: {}", ip);
    Bandwidth bandwidth =
        Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofSeconds(30)).build();
    return Bucket.builder().addLimit(bandwidth).build();
  }
}
