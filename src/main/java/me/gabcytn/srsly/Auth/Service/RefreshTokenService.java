package me.gabcytn.srsly.Auth.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Auth.DTO.RefreshTokenValidatorDto;
import me.gabcytn.srsly.Auth.Repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final CookieManipulatorService cookieManipulatorService;

  public void save(RefreshTokenValidatorDto tokenValidator) {
    refreshTokenRepository.save(tokenValidator);
  }

  public Optional<RefreshTokenValidatorDto> find(String key) {
    return refreshTokenRepository.findById(key);
  }

  public void delete(String key) {
    refreshTokenRepository.deleteById(key);
    cookieManipulatorService.deleteRefreshToken();
  }

  public Boolean exists(String key) {
    return refreshTokenRepository.existsById(key);
  }

  public String generateRefreshToken() {
    String refreshToken = hashString(generateRandomString());
    cookieManipulatorService.setRefreshToken(refreshToken);
    return refreshToken;
  }

  private String hashString(String text) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      byte[] hash = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException exception) {
      log.error("Error generating refresh token");
      log.error(exception.getMessage());
      return "";
    }
  }

  private String generateRandomString() {
    byte[] byteArray = new byte[32];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(byteArray);
    StringBuilder sb = new StringBuilder();
    for (byte b : byteArray) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
