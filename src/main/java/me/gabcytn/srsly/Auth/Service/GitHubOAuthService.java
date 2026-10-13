package me.gabcytn.srsly.Auth.Service;

import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.GitHubAccessTokenResponse;
import me.gabcytn.srsly.Auth.DTO.Oauth.GitHubApiAuthorizationRequest;
import me.gabcytn.srsly.Auth.DTO.Oauth.GitHubUser;
import me.gabcytn.srsly.Auth.DTO.Oauth.SignUpMethod;
import me.gabcytn.srsly.Auth.Proxy.GitHubAccessTokenRetrievalProxy;
import me.gabcytn.srsly.Auth.Proxy.GitHubUserInfoProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("GitHubOAuthService")
public class GitHubOAuthService extends OAuthService {

  private final GitHubAccessTokenRetrievalProxy accessTokenRetrievalProxy;
  private final GitHubUserInfoProxy gitHubUserInfoProxy;

  public GitHubOAuthService(
      UserService userService,
      JwtService jwtService,
      GitHubAccessTokenRetrievalProxy gitHubAccessTokenRetrievalProxy,
      GitHubUserInfoProxy gitHubUserInfoProxy) {
    super(userService, jwtService);
    this.accessTokenRetrievalProxy = gitHubAccessTokenRetrievalProxy;
    this.gitHubUserInfoProxy = gitHubUserInfoProxy;
  }

  @Override
  public AuthResponse authorize(String code) {
    String accessToken = getAccessToken(code);
    String email = getEmailFromGitHub(accessToken);

    return authenticate(email, SignUpMethod.GITHUB);
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
}
