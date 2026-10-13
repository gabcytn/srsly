package me.gabcytn.srsly.Auth.Service;

import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.GoogleApiAuthorizationRequest;
import me.gabcytn.srsly.Auth.DTO.Oauth.GoogleApiAuthorizationResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.SignUpMethod;
import me.gabcytn.srsly.Auth.Proxy.GoogleProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("GoogleOAuthService")
public class GoogleOAuthService extends OAuthService {
  private final GoogleProxy googleProxy;
  private final GoogleJwtService googleJwtService;

  public GoogleOAuthService(
      UserService userService,
      JwtService jwtService,
      GoogleProxy googleProxy,
      GoogleJwtService googleJwtService) {
    super(userService, jwtService);
    this.googleProxy = googleProxy;
    this.googleJwtService = googleJwtService;
  }

  @Override
  public AuthResponse authorize(String code) {
    GoogleApiAuthorizationResponse response = authorizeUser(code);
    String email = extractEmailFromGoogleJwt(response);

    return authenticate(email, SignUpMethod.GOOGLE);
  }

  private GoogleApiAuthorizationResponse authorizeUser(String code) {
    GoogleApiAuthorizationRequest request = new GoogleApiAuthorizationRequest(code);
    return googleProxy.authorize(request);
  }

  private String extractEmailFromGoogleJwt(GoogleApiAuthorizationResponse response) {
    return googleJwtService.extractEmail(response.id_token());
  }
}
