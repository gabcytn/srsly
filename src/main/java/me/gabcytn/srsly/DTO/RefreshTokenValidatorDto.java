package me.gabcytn.srsly.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("refreshToken")
@Getter
@Setter
public class RefreshTokenValidatorDto
{
  @Id private String key;
  private String email;
  private String deviceName;
  @TimeToLive private Long expiresAt;

  public RefreshTokenValidatorDto(String key, String email, String deviceName) {
    this.key = key;
    this.email = email;
    this.deviceName = deviceName;
  }
}
