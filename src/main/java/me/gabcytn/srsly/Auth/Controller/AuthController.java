package me.gabcytn.srsly.Auth.Controller;

import jakarta.validation.Valid;
import me.gabcytn.srsly.Auth.DTO.AuthUserDto;
import me.gabcytn.srsly.Auth.DTO.LoginResponseDto;
import me.gabcytn.srsly.Auth.DTO.RefreshTokenRequestDto;
import me.gabcytn.srsly.Auth.Service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public LoginResponseDto register(@RequestBody @Valid AuthUserDto user) {
    LoginResponseDto response = authService.signup(user);
    authService.generateRefreshToken(user.getEmail(), user.getDeviceName());
    return response;
  }

  @PostMapping("/login")
  public LoginResponseDto login(
      @RequestBody @Valid AuthUserDto user,
      @CookieValue(value = "X-REFRESH-TOKEN", required = false) String refreshToken) {
    LoginResponseDto response = authService.authenticate(user);
    if (refreshToken == null) {
      authService.generateRefreshToken(user.getEmail(), user.getDeviceName());
    }
    return response;
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<LoginResponseDto> refreshToken(
      @RequestBody @Valid RefreshTokenRequestDto tokenRequest,
      @CookieValue(value = "X-REFRESH-TOKEN") String refreshToken) {
    LoginResponseDto responseDto = authService.newJwt(refreshToken, tokenRequest.getDeviceName());
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
