package me.gabcytn.srsly.Auth.Service;

import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("GitHubOAuthService")
public class GitHubOAuthService extends OAuthService {
  @Override
  public AuthResponse authenticate(String code) {
    // TODO: implement github authentication
    throw new RuntimeException("To be implemented.");
  }
}
