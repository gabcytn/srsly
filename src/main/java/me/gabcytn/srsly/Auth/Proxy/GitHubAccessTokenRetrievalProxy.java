package me.gabcytn.srsly.Auth.Proxy;

import me.gabcytn.srsly.Auth.DTO.Oauth.GitHubAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "https://github.com", name = "github-access-token-retrieval")
public interface GitHubAccessTokenRetrievalProxy {
  @PostMapping(
      value = "/login/oauth/access_token",
      headers = {"Accept=application/json"})
  GitHubAccessTokenResponse getAccessToken(
      @RequestParam("client_id") String clientId,
      @RequestParam("client_secret") String clientSecret,
      @RequestParam("code") String code);
}
