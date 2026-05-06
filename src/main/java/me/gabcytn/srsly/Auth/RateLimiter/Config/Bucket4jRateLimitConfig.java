package me.gabcytn.srsly.Auth.RateLimiter.Config;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Auth.RateLimiter.EndpointLimit;
import me.gabcytn.srsly.Auth.RateLimiter.Interceptor.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class Bucket4jRateLimitConfig implements WebMvcConfigurer {
  private final RateLimitInterceptor interceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(interceptor).addPathPatterns(endpoints());
  }

  private List<String> endpoints() {
    EndpointLimit[] limits = EndpointLimit.values();
    List<String> endpoints = new ArrayList<>();

    for (EndpointLimit limit : limits) {
      endpoints.add(prefixBase(limit.path));
    }

    return endpoints;
  }

  private String prefixBase(String toAppend) {
    String base = "/api/v1/public/auth";
    return base + toAppend;
  }
}
