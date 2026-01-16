package me.gabcytn.srsly.Service;

import java.util.Optional;
import lombok.AllArgsConstructor;
import me.gabcytn.srsly.DTO.LoginResponseDto;
import me.gabcytn.srsly.DTO.LoginUserDto;
import me.gabcytn.srsly.DTO.RefreshTokenValidatorDto;
import me.gabcytn.srsly.DTO.RegisterUserDto;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.DuplicateEmailException;
import me.gabcytn.srsly.Exception.RefreshTokenException;
import me.gabcytn.srsly.Exception.UnauthenticatedException;
import me.gabcytn.srsly.Repository.UserRepository;
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
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;

  public void signup(RegisterUserDto user) {
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new DuplicateEmailException();
    }
    User toSave =
        User.ofEmailAndPassword(user.getEmail(), passwordEncoder.encode(user.getPassword()));
    userRepository.save(toSave);
  }

  public LoginResponseDto authenticate(LoginUserDto user, Optional<String> refreshToken) {
    Authentication authToken =
        new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);

    if (!authentication.isAuthenticated()) throw new UnauthenticatedException("User not found");

    String token = jwtService.generateToken(user.getEmail());

    if (refreshToken.isEmpty()) {
      String generatedRefreshToken = jwtService.generateRefreshToken();
      RefreshTokenValidatorDto tokenValidatorDto =
          new RefreshTokenValidatorDto(
              generatedRefreshToken, user.getEmail(), user.getDeviceName());
      refreshTokenService.save(tokenValidatorDto);
    }
    return new LoginResponseDto(token, jwtService.getExpirationTime());
  }

  public LoginResponseDto newJwt(String refreshToken, String deviceName) {
    try {
      RefreshTokenValidatorDto validator = refreshTokenService.find(refreshToken);
      if (validator == null) throw new RefreshTokenException("Refresh token not found.");
      if (!deviceName.equals(validator.getDeviceName()))
        throw new RefreshTokenException("Stored device name does not match request's device name");
      String jwt = jwtService.generateToken(validator.getEmail());

      refreshTokenService.delete(refreshToken);
      String generatedRefreshToken = jwtService.generateRefreshToken();
      validator.setKey(generatedRefreshToken);
      refreshTokenService.save(validator);
      return new LoginResponseDto(jwt, jwtService.getExpirationTime());
    } catch (Exception e) {
      LOG.error("Error generating new JWT");
      LOG.error(e.getMessage());
      throw new RefreshTokenException(e.getMessage());
    }
  }
}
