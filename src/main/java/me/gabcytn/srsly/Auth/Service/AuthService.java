package me.gabcytn.srsly.Auth.Service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import me.gabcytn.srsly.Auth.DTO.AuthUserDto;
import me.gabcytn.srsly.Auth.DTO.JwtResponse;
import me.gabcytn.srsly.Auth.DTO.RefreshTokenValidatorDto;
import me.gabcytn.srsly.Auth.Exception.DuplicateEmailException;
import me.gabcytn.srsly.Auth.Exception.RefreshTokenException;
import me.gabcytn.srsly.Auth.Exception.UnauthenticatedException;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
  private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public AuthResponse signup(AuthUserDto user) {
    if (userService.existsByEmail(user.getEmail())) {
      throw new DuplicateEmailException();
    }
    User toSave =
        User.ofEmailAndPassword(user.getEmail(), passwordEncoder.encode(user.getPassword()));
    userService.save(toSave);
    return this.authenticate(user);
  }

  public AuthResponse authenticate(AuthUserDto user) {
    Authentication authToken =
        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);

    if (!authentication.isAuthenticated()) throw new UnauthenticatedException("User not found");

    String token = jwtService.generateToken(user.getEmail());
    JwtResponse jwt = new JwtResponse(token, jwtService.getExpirationTime());
    User userFromDb = userService.findByEmail(user.getEmail());
    return new AuthResponse(userFromDb.getEmail(), userFromDb.getEmailVerifiedAt() != null, jwt);
  }

  public void generateRefreshToken(String userEmail, String userDeviceName) {
    String generatedRefreshToken = refreshTokenService.generateRefreshToken();
    RefreshTokenValidatorDto tokenValidatorDto =
        new RefreshTokenValidatorDto(generatedRefreshToken, userEmail, userDeviceName);
    refreshTokenService.save(tokenValidatorDto);
  }

  public JwtResponse newJwt(String refreshTokenKey, String requestDeviceName) {
    Optional<RefreshTokenValidatorDto> optionalValidator =
        refreshTokenService.find(refreshTokenKey);
    if (optionalValidator.isEmpty()) {
      LOG.error("Null validator.");
      throw new RefreshTokenException("Refresh token not found.");
    }
    RefreshTokenValidatorDto validator = optionalValidator.get();
    if (!requestDeviceName.equals(validator.getDeviceName())) {
      LOG.error("Device names don't match.");
      throw new RefreshTokenException("Stored device name does not match request's device name.");
    }
    refreshTokenService.delete(refreshTokenKey);
    generateRefreshToken(validator.getEmail(), validator.getDeviceName());
    String jwt = jwtService.generateToken(validator.getEmail());
    return new JwtResponse(jwt, jwtService.getExpirationTime());
  }

  public void invalidateRefreshToken(String refreshToken) {
    refreshTokenService.delete(refreshToken);
  }
}
