package me.gabcytn.srsly.Service;

import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.RefreshTokenValidatorDto;
import me.gabcytn.srsly.Repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;

  public void save(RefreshTokenValidatorDto tokenValidator) {
    refreshTokenRepository.save(tokenValidator);
  }

  public RefreshTokenValidatorDto find(String key) {
    return refreshTokenRepository.findById(key).orElseThrow();
  }

  public void delete(String key) {
    refreshTokenRepository.deleteById(key);
  }
}
