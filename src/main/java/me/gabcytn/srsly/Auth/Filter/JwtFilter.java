package me.gabcytn.srsly.Auth.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Auth.Service.JwtService;
import me.gabcytn.srsly.Auth.Service.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsServiceImpl userDetailsServiceImpl;
  private final HandlerExceptionResolver handlerExceptionResolver;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String authorizationHeader = request.getHeader("Authorization");
    if (request.getRequestURI().startsWith("/api/v1/auth")) {
      log.info("Disregard filter. User is trying to authenticate.");
      filterChain.doFilter(request, response);
      return;
    }
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      log.info("No auth header / doesn't start with Bearer");
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final String token = authorizationHeader.substring(7);
      final String userEmail = jwtService.extractUsername(token);
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // early return if email is invalid or already authenticated
      if (userEmail == null || authentication != null) {
        log.info("user email is null OR authentication is NOT NULL");
        filterChain.doFilter(request, response);
        return;
      }

      UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userEmail);

      // early return if token is invalid
      if (!jwtService.isTokenValid(token, userDetails)) {
        log.error("Invalid token");
        filterChain.doFilter(request, response);
        return;
      }

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      log.error(e.getMessage());
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }
}
