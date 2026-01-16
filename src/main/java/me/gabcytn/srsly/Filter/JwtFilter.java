package me.gabcytn.srsly.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Service.JwtService;
import me.gabcytn.srsly.Service.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final HandlerExceptionResolver handlerExceptionResolver;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    final String authorizationHeader = request.getHeader("Authorization");
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      LOGGER.info("No auth header / doesn't start with Bearer");
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final String token = authorizationHeader.substring(7);
      final String userEmail = jwtService.extractUsername(token);
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      // early return if email is invalid or already authenticated
      if (userEmail == null || authentication != null) {
        LOGGER.info("user email is null OR authentication is NOT NULL");
        filterChain.doFilter(request, response);
        return;
      }

      UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

      // early return if token is invalid
      if (!jwtService.isTokenValid(token, userDetails)) {
        System.err.println("Token is invalid");
        filterChain.doFilter(request, response);
        return;
      }

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      System.err.println("Error in jwt filter");
      System.err.println(e.getMessage());
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }
}
