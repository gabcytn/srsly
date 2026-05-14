package me.gabcytn.srsly.Auth.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CookieManipulatorService {
  private final HttpServletResponse response;

  public void setRefreshToken(String token) {
    Cookie cookie = new Cookie("X-REFRESH-TOKEN", token);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    cookie.setAttribute("SameSite", "None");
    cookie.setMaxAge(604800);
    response.addCookie(cookie);
  }

  public void deleteRefreshToken() {
    Cookie cookie = new Cookie("X-REFRESH-TOKEN", null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(true);
    cookie.setAttribute("SameSite", "None");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }
}
