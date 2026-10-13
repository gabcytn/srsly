package me.gabcytn.srsly.Auth.Service;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import me.gabcytn.srsly.Auth.DTO.JwtResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.GoogleApiAuthorizationRequest;
import me.gabcytn.srsly.Auth.DTO.Oauth.GoogleApiAuthorizationResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.SignUpMethod;
import me.gabcytn.srsly.Auth.Entity.User;
import me.gabcytn.srsly.Auth.Proxy.GoogleProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Qualifier("GoogleOAuthService")
public class GoogleOAuthService extends OAuthService {
  private final GoogleProxy googleProxy;
  private final GoogleJwtService googleJwtService;
  private final JwtService jwtService;
  private final UserService userService;

  @Override
  public AuthResponse authenticate(String code) {
    GoogleApiAuthorizationRequest request = new GoogleApiAuthorizationRequest(code);
    GoogleApiAuthorizationResponse response = googleProxy.authorize(request);

    String email = googleJwtService.extractEmail(response.id_token());
    this.setSecurityContextHolder(email);

    if (!userService.existsByEmail(email)) {
      User user = User.ofEmailAndSignUpMethod(email, SignUpMethod.GOOGLE);
      userService.save(user);
    }

    if (!userService
        .findByEmail(email)
        .getSignUpMethod()
        .equals(SignUpMethod.GOOGLE.getProvider())) {
      throw new IllegalStateException("Invalid SignUp provider");
    }

    String token = jwtService.generateToken(email);
    JwtResponse jwtResponse = new JwtResponse(token, jwtService.getExpirationTime());

    return new AuthResponse(email, jwtResponse);
  }
}
