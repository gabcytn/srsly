package me.gabcytn.srsly.Auth.RateLimiter.Interceptor;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Auth.RateLimiter.Service.RateLimitingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

  private final RateLimitingService rateLimitingService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws IOException {
    String clientIp = request.getHeader("X-FORWARDED-FOR");
    if (clientIp == null || clientIp.isEmpty()) {
      clientIp = request.getRemoteAddr();
    }

    String endpoint = request.getRequestURI();
    Bucket bucket = rateLimitingService.findOrCreateBucketByIpAndEndpoint(clientIp, endpoint);
    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

    if (probe.isConsumed()) {
      response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
      return true;
    }

    long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
    response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    return false;
  }
}
