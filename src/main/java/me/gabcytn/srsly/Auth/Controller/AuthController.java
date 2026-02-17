package me.gabcytn.srsly.Auth.Controller;

import jakarta.validation.Valid;
import me.gabcytn.srsly.Auth.DTO.AuthUserDto;
import me.gabcytn.srsly.Auth.DTO.JwtResponse;
import me.gabcytn.srsly.Auth.DTO.RefreshTokenRequestDto;
import me.gabcytn.srsly.Auth.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public JwtResponse register(@RequestBody @Valid AuthUserDto user) {
    JwtResponse response = authService.signup(user);
    authService.generateRefreshToken(user.getEmail(), user.getDeviceName());
    return response;
  }

  @PostMapping("/login")
  public JwtResponse login(
      @RequestBody @Valid AuthUserDto user,
      @CookieValue(value = "X-REFRESH-TOKEN", required = false) String refreshToken) {
    JwtResponse response = authService.authenticate(user);
    if (refreshToken == null) {
      authService.generateRefreshToken(user.getEmail(), user.getDeviceName());
    }
    return response;
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<JwtResponse> refreshToken(
      @RequestBody @Valid RefreshTokenRequestDto tokenRequest,
      @CookieValue(value = "X-REFRESH-TOKEN") String refreshToken) {
    JwtResponse responseDto = authService.newJwt(refreshToken, tokenRequest.getDeviceName());
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
