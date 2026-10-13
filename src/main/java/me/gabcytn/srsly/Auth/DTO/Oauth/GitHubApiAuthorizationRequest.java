package me.gabcytn.srsly.Auth.DTO.Oauth;

import lombok.Getter;
import me.gabcytn.srsly.Util.ApplicationContextProvider;

@Getter
public class GitHubApiAuthorizationRequest {
  private String code;
  private String clientId;
  private String clientSecret;

  public GitHubApiAuthorizationRequest(String code) {
    this.code = code;
    this.clientId = ApplicationContextProvider.getProperty("security.oauth.github.client-id");
    this.clientSecret =
        ApplicationContextProvider.getProperty("security.oauth.github.client-secret");
  }
}
