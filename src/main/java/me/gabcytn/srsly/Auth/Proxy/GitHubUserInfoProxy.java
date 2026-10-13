package me.gabcytn.srsly.Auth.Proxy;

import me.gabcytn.srsly.Auth.DTO.Oauth.GitHubUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(url = "https://api.github.com", name = "github-user-info")
public interface GitHubUserInfoProxy {
  @GetMapping(
      value = "/user",
      headers = {"Accept=application/vnd.github+json"})
  GitHubUser retrieveUser(@RequestHeader("Authorization") String authorizationHeader);
}
