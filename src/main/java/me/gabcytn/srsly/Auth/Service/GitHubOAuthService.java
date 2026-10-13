package me.gabcytn.srsly.Auth.Service;

import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import me.gabcytn.srsly.Auth.DTO.JwtResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.GitHubAccessTokenResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.GitHubApiAuthorizationRequest;
import me.gabcytn.srsly.Auth.DTO.Oauth.GitHubUser;
import me.gabcytn.srsly.Auth.DTO.Oauth.SignUpMethod;
import me.gabcytn.srsly.Auth.Entity.User;
import me.gabcytn.srsly.Auth.Proxy.GitHubAccessTokenRetrievalProxy;
import me.gabcytn.srsly.Auth.Proxy.GitHubUserInfoProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("GitHubOAuthService")
public class GitHubOAuthService extends OAuthService {

  private final GitHubAccessTokenRetrievalProxy accessTokenRetrievalProxy;
  private final GitHubUserInfoProxy gitHubUserInfoProxy;
  private final UserService userService;
  private final JwtService jwtService;

  public GitHubOAuthService(
      GitHubAccessTokenRetrievalProxy gitHubAccessTokenRetrievalProxy,
      GitHubUserInfoProxy gitHubUserInfoProxy,
      UserService userService,
      JwtService jwtService) {
    this.accessTokenRetrievalProxy = gitHubAccessTokenRetrievalProxy;
    this.gitHubUserInfoProxy = gitHubUserInfoProxy;
    this.userService = userService;
    this.jwtService = jwtService;
  }

  @Override
  public AuthResponse authenticate(String code) {
    GitHubApiAuthorizationRequest request = new GitHubApiAuthorizationRequest(code);
    String id = request.getClientId(), secret = request.getClientSecret();
    GitHubAccessTokenResponse tokenResponse =
        accessTokenRetrievalProxy.getAccessToken(id, secret, code);

    String accessToken = tokenResponse.access_token();
    GitHubUser gitHubUser = gitHubUserInfoProxy.retrieveUser("Bearer " + accessToken);

    String email = gitHubUser.email();

    if (!userService.existsByEmail(email)) {
      User user = User.ofEmailAndSignUpMethod(email, SignUpMethod.GITHUB);
      userService.save(user);
    }

    // TODO: check if same email is used across different providers

    String token = jwtService.generateToken(email);
    JwtResponse jwtResponse = new JwtResponse(token, jwtService.getExpirationTime());

    this.setSecurityContextHolder(email);
    return new AuthResponse(email, jwtResponse);
  }
}
