package me.gabcytn.srsly.Auth.Service;

import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import me.gabcytn.srsly.Auth.DTO.JwtResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.SignUpMethod;
import me.gabcytn.srsly.Auth.Entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class OAuthService {
  protected final UserService userService;
  protected final JwtService jwtService;

  public OAuthService(UserService userService, JwtService jwtService) {
    this.userService = userService;
    this.jwtService = jwtService;
  }

  public abstract AuthResponse authorize(String code);

  protected final AuthResponse authenticate(String email, SignUpMethod signUpMethod) {
    if (this.isUserSigningUp(email)) {
      this.userSignUp(email);
    }

    verifyEmailAndProviderIntegrity(email, signUpMethod);

    this.setSecurityContextHolder(email);
    return new AuthResponse(email, generateJwt(email));
  }

  private boolean isUserSigningUp(String email) {
    return !userService.existsByEmail(email);
  }

  private void userSignUp(String email) {
    User user = User.ofEmailAndSignUpMethod(email, SignUpMethod.GITHUB);
    userService.save(user);
  }

  private void verifyEmailAndProviderIntegrity(String email, SignUpMethod signUpMethod) {
    if (!userService.findByEmail(email).getSignUpMethod().equals(signUpMethod.getProvider())) {
      throw new IllegalStateException("Email is already associated with another provider.");
    }
  }

  private JwtResponse generateJwt(String email) {
    String token = jwtService.generateToken(email);
    return new JwtResponse(token, jwtService.getExpirationTime());
  }

  private void setSecurityContextHolder(String username) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
