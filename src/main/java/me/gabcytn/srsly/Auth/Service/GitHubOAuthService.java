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
    String accessToken = getAccessToken(code);
    String email = getEmailFromGitHub(accessToken);

    if (isUserSigningUp(email)) {
      userSignUp(email);
    }

    // TODO: check if same email is used across different providers

    this.setSecurityContextHolder(email);
    return new AuthResponse(email, generateJwt(email));
  }

  private String getAccessToken(String code) {
    GitHubApiAuthorizationRequest request = new GitHubApiAuthorizationRequest(code);
    GitHubAccessTokenResponse tokenResponse =
        accessTokenRetrievalProxy.getAccessToken(
            request.getClientId(), request.getClientSecret(), request.getCode());

    return tokenResponse.access_token();
  }

  private String getEmailFromGitHub(String accessToken) {
    GitHubUser gitHubUser = gitHubUserInfoProxy.retrieveUser("Bearer " + accessToken);
    return gitHubUser.email();
  }

  private boolean isUserSigningUp(String email) {
    return !userService.existsByEmail(email);
  }

  private void userSignUp(String email) {
    User user = User.ofEmailAndSignUpMethod(email, SignUpMethod.GITHUB);
    userService.save(user);
  }

  private JwtResponse generateJwt(String email) {
    String token = jwtService.generateToken(email);
    return new JwtResponse(token, jwtService.getExpirationTime());
  }
}
