package me.gabcytn.srsly.Auth.Proxy;

import me.gabcytn.srsly.Auth.DTO.Oauth.GoogleApiAuthorizationRequest;
import me.gabcytn.srsly.Auth.DTO.Oauth.GoogleApiAuthorizationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "https://oauth2.googleapis.com", name = "google")
public interface GoogleProxy {
  @PostMapping("/token")
  GoogleApiAuthorizationResponse authorize(@RequestBody GoogleApiAuthorizationRequest googleApiAuthorizationRequest);
}
