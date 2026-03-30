package me.gabcytn.srsly.Auth.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailJwtService extends JwtService {

  public EmailJwtService(@Value("${security.jwt.email-secret-key}") String secretKey) {
    this.secretKey = secretKey;
    this.JWT_TYPE = Type.EMAIL_VERIFICATION;
  }
}
