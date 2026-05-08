package me.gabcytn.srsly.Auth.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

  @Tag(name = "Authentication")
  @Operation(summary = "Sign up", description = "Authenticates and returns details and JWT.")
  @PostMapping("/register")
  @ApiResponse(responseCode = "201")
  public AuthResponse register(@RequestBody @Valid AuthUserDto user) {
    AuthResponse response = authService.signup(user);
    authService.generateRefreshToken(user.getEmail(), user.getDeviceName());
    return response;
  }

  @Tag(name = "Authentication")
  @Operation(summary = "Login", description = "Returns basic user details and JWT.")
  @ApiResponse(responseCode = "200")
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

  @Tag(name = "Authentication")
  @Operation(summary = "Logout", description = "Deletes refresh token from repository.")
  @ApiResponse(responseCode = "200")
  @PostMapping("/logout")
  public void logout(
      @CookieValue(value = "X-REFRESH-TOKEN", required = false) String refreshToken) {
    if (refreshToken != null) {
      authService.invalidateRefreshToken(refreshToken);
    }
  }

  @Tag(name = "Authentication")
  @Operation(summary = "Request new JWT", description = "Rotates refresh token.")
  @ApiResponse(responseCode = "200")
  @PostMapping("/refresh-token")
  public ResponseEntity<JwtResponse> refreshToken(
      @RequestBody @Valid RefreshTokenRequestDto tokenRequest,
      @CookieValue(value = "X-REFRESH-TOKEN") String refreshToken) {
    JwtResponse responseDto = authService.newJwt(refreshToken, tokenRequest.getDeviceName());
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }
}
