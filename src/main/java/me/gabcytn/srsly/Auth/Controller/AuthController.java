package me.gabcytn.srsly.Auth.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.gabcytn.srsly.Auth.DTO.*;
import me.gabcytn.srsly.Auth.DTO.Oauth.OAuthCallbackRequest;
import me.gabcytn.srsly.Auth.Service.AuthService;
import me.gabcytn.srsly.Auth.Service.GitHubOAuthService;
import me.gabcytn.srsly.Auth.Service.OAuthService;
import me.gabcytn.srsly.Auth.Service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/auth")
public class AuthController {
  private final AuthService authService;
  private final OAuthService googleOAuthService;
  private final OAuthService gitHubOAuthService;
  private final RefreshTokenService refreshTokenService;

  public AuthController(
      AuthService authService,
      @Qualifier("GoogleOAuthService") OAuthService googleOAuthService,
      @Qualifier("GitHubOAuthService") GitHubOAuthService gitHubOAuthService,
      RefreshTokenService refreshTokenService) {
    this.authService = authService;
    this.googleOAuthService = googleOAuthService;
    this.gitHubOAuthService = gitHubOAuthService;
    this.refreshTokenService = refreshTokenService;
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
      @CookieValue(value = "X-REFRESH-TOKEN") String refreshToken) {
    JwtResponse responseDto = authService.newJwt(refreshToken);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  @Tag(name = "Authentication")
  @Operation(summary = "Sign in with GitHub callback")
  @PostMapping("/oauth/callback/github")
  public AuthResponse githubCallback(
      @RequestBody @Valid OAuthCallbackRequest request,
      @CookieValue(value = "X-REFRESH-TOKEN", required = false) String refreshToken) {
    AuthResponse response = gitHubOAuthService.authorize(request.code());

    if (refreshToken == null || !refreshTokenService.exists(refreshToken))
      authService.generateRefreshToken(response.email());

    return response;
  }

  @Tag(name = "Authentication")
  @Operation(summary = "Sign in with Google callback")
  @PostMapping("/oauth/callback/google")
  public AuthResponse googleCallback(
      @RequestBody @Valid OAuthCallbackRequest request,
      @CookieValue(value = "X-REFRESH-TOKEN", required = false) String refreshToken) {
    AuthResponse response = googleOAuthService.authorize(request.code());

    if (refreshToken == null || !refreshTokenService.exists(refreshToken))
      authService.generateRefreshToken(response.email());

    return response;
  }
}
