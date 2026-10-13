package me.gabcytn.srsly.Auth.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("refreshToken")
@Getter
@Setter
public class RefreshTokenValidatorDto {
  @Id private String key;
  private String email;
  @TimeToLive private Long expiresAt;

  public RefreshTokenValidatorDto(String key, String email) {
    this.key = key;
    this.email = email;
    this.expiresAt = 604800L;
  }
}
