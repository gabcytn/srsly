package me.gabcytn.srsly.Auth.RateLimiter.Service;

import io.github.bucket4j.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Auth.RateLimiter.EndpointLimit;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RateLimitingService {
  // TODO: use redis
  private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

  public Bucket findOrCreateBucketByIpAndEndpoint(String ip, String endpoint) {
    String key = "ip={" + ip + "}:" + "path={" + endpoint + "}";
    return cache.computeIfAbsent(key, (k) -> newBucket(ip, endpoint));
  }

  private Bucket newBucket(String ip, String endpoint) {
    log.info("Creating bucket for IP: {}", ip);
    return Bucket.builder().addLimit(EndpointLimit.resolve(endpoint)).build();
  }
}
