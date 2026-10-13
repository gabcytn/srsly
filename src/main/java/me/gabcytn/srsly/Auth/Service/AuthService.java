package me.gabcytn.srsly.Auth.Service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.Auth.DTO.JwtResponse;
import me.gabcytn.srsly.Auth.DTO.RefreshTokenValidatorDto;
import me.gabcytn.srsly.Auth.Exception.RefreshTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
  private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public void generateRefreshToken(String userEmail) {
    String generatedRefreshToken = refreshTokenService.generateRefreshToken();
    RefreshTokenValidatorDto tokenValidatorDto =
        new RefreshTokenValidatorDto(generatedRefreshToken, userEmail);
    refreshTokenService.save(tokenValidatorDto);
  }

  public JwtResponse newJwt(String refreshTokenKey) {
    Optional<RefreshTokenValidatorDto> optionalValidator =
        refreshTokenService.find(refreshTokenKey);
    if (optionalValidator.isEmpty()) {
      LOG.error("Null validator.");
      throw new RefreshTokenException("Refresh token not found.");
    }
    RefreshTokenValidatorDto validator = optionalValidator.get();
    refreshTokenService.delete(refreshTokenKey);
    generateRefreshToken(validator.getEmail());
    String jwt = jwtService.generateToken(validator.getEmail());
    return new JwtResponse(jwt, jwtService.getExpirationTime());
  }

  public void invalidateRefreshToken(String refreshToken) {
    refreshTokenService.delete(refreshToken);
  }
}
