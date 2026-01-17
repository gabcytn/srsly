package me.gabcytn.srsly.Controller;

import jakarta.validation.Valid;
import me.gabcytn.srsly.DTO.LoginResponseDto;
import me.gabcytn.srsly.DTO.LoginUserDto;
import me.gabcytn.srsly.DTO.RefreshTokenRequestDto;
import me.gabcytn.srsly.DTO.RegisterUserDto;
import me.gabcytn.srsly.Service.AuthService;
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
  public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDto user) {
    authService.signup(user);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public LoginResponseDto login(
      @RequestBody @Valid LoginUserDto user,
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
