package me.gabcytn.srsly.Auth.Service;

import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import lombok.AllArgsConstructor;
import me.gabcytn.srsly.Auth.DTO.RefreshTokenValidatorDto;
import me.gabcytn.srsly.Auth.Repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RefreshTokenService {
  private final RefreshTokenRepository refreshTokenRepository;
  private final HttpServletResponse response;

  public void save(RefreshTokenValidatorDto tokenValidator) {
    refreshTokenRepository.save(tokenValidator);
  }

  public Optional<RefreshTokenValidatorDto> find(String key) {
    return refreshTokenRepository.findById(key);
  }

  public void delete(String key) {
    refreshTokenRepository.deleteById(key);
  }

  public String generateRefreshToken() {
    String refreshToken = hashString(generateRandomString());
    sendRefreshTokenInResponseCookie(refreshToken);
    return refreshToken;
  }

  private void sendRefreshTokenInResponseCookie(String refreshToken) {
    Cookie cookie = new Cookie("X-REFRESH-TOKEN", refreshToken);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(604800);
    response.addCookie(cookie);
  }

  private String hashString(String text) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      byte[] hash = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException exception) {
      System.err.println("Error generating refresh token");
      System.err.println(exception.getMessage());
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
