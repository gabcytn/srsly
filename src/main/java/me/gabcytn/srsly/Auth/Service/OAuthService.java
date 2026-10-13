package me.gabcytn.srsly.Auth.Service;

import me.gabcytn.srsly.Auth.DTO.AuthResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class OAuthService {
  public final void setSecurityContextHolder(String username) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  public abstract AuthResponse authenticate(String code);
}
