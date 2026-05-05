package me.gabcytn.srsly.Auth.Config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {
  @Bean
  public LettuceConnectionFactory lettuceConnectionFactory() {
    RedisProperties properties = redisProperties();
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();

    configuration.setHostName(properties.getHost());
    configuration.setPort(properties.getPort());

    return new LettuceConnectionFactory(configuration);
  }

  // NOTE: this is unused for now (for caching purposes)
  @Bean
  public RedisCacheManager cacheManager() {
    RedisCacheConfiguration configuration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(15))
            .disableCachingNullValues()
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()));

    return RedisCacheManager.builder(lettuceConnectionFactory())
        .cacheDefaults(configuration)
        .build();
  }

  @Bean
  @Primary
  public RedisProperties redisProperties() {
    return new RedisProperties();
  }
}
