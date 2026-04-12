package me.gabcytn.srsly.Auth.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailJwtService extends JwtService {

  public EmailJwtService(
      @Value("${security.jwt.secret.email}") String secretKey,
      @Value("${security.jwt.expiration.email}") Long expirationTime) {
    this.secretKey = secretKey;
    this.jwtExpiration = expirationTime;
    this.JWT_TYPE = Type.EMAIL_VERIFICATION;
  }
}
