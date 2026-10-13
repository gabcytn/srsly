package me.gabcytn.srsly.Auth.DTO.Oauth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.gabcytn.srsly.Util.ApplicationContextProvider;

@Getter
public class GoogleApiAuthorizationRequest {
  private String code;

  private String client_id;

  private String client_secret;

  private String grant_type = "authorization_code";

  @Setter(AccessLevel.NONE)
  private String redirect_uri;

  public GoogleApiAuthorizationRequest(String code) {
    this.code = code;
    this.client_id = ApplicationContextProvider.getProperty("security.oauth.google.client-id");
    this.client_secret =
        ApplicationContextProvider.getProperty("security.oauth.google.client-secret");
    this.redirect_uri =
        ApplicationContextProvider.getProperty("security.oauth.google.redirect-uri");
  }
}
