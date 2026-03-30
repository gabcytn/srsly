package me.gabcytn.srsly.Auth.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtService {
  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.expiration-time}")
  private long jwtExpiration;

  public String extractUsername(String token) {
    String claim = extractClaim(token, Claims::getSubject);
    log.info("Claims::getSubject -> {}", claim);
    return claim;
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    log.info("claims -> {}", claims);
    T val = claimsResolver.apply(claims);
    log.info("date -> {}", val);
    return val;
  }

  public String generateEmailVerificationToken(String email) {
    return generateToken(new HashMap<>(Map.of("type", "email_verification")), email);
  }

  public String generateToken(String username) {
    return generateToken(new HashMap<>(Map.of("type", "authentication")), username);
  }

  public String generateToken(Map<String, Object> extraClaims, String username) {
    return buildToken(extraClaims, username, jwtExpiration);
  }

  public long getExpirationTime() {
    return jwtExpiration;
  }

  private String buildToken(Map<String, Object> extraClaims, String username, long expiration) {
    return Jwts.builder()
        .claims(extraClaims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSignInKey())
        .compact();
  }

  public boolean isEmailVerificationTokenValid(String token, UserDetails userDetails) {
    final String email = extractUsername(token);
    String tokenType = extractClaim(token, claims -> claims.get("type", String.class));
    if (tokenType.equals("email_verification")) {
      return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    log.error("Invalid email verification token type.");
    return false;
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
