package me.gabcytn.srsly.Auth.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import me.gabcytn.srsly.Auth.DTO.AuthUserDto;
import me.gabcytn.srsly.Auth.DTO.JwtResponse;
import me.gabcytn.srsly.Auth.DTO.RefreshTokenRequestDto;
import me.gabcytn.srsly.Auth.Service.AuthService;
import me.gabcytn.srsly.Auth.Service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/public/auth")
public class AuthController {
  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  @PostMapping("/register")
  public AuthResponse register(@RequestBody @Valid AuthUserDto user) {
    AuthResponse response = authService.signup(user);
    authService.generateRefreshToken(user.getEmail(), user.getDeviceName());
    return response;
  }

  @PostMapping("/login")
  public AuthResponse login(
      @RequestBody @Valid AuthUserDto user,
      @CookieValue(value = "X-REFRESH-TOKEN", required = false) String refreshToken) {
    AuthResponse response = authService.authenticate(user);
    if (refreshToken == null || !refreshTokenService.exists(refreshToken)) {
      authService.generateRefreshToken(user.getEmail(), user.getDeviceName());
    }
    return response;
  }

  @PostMapping("/logout")
  public void logout(
      @CookieValue(value = "X-REFRESH-TOKEN", required = false) String refreshToken) {
    if (refreshToken != null) {
      authService.invalidateRefreshToken(refreshToken);
    }
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<JwtResponse> refreshToken(
      @RequestBody @Valid RefreshTokenRequestDto tokenRequest,
      @CookieValue(value = "X-REFRESH-TOKEN") String refreshToken) {
    JwtResponse responseDto = authService.newJwt(refreshToken, tokenRequest.getDeviceName());
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
