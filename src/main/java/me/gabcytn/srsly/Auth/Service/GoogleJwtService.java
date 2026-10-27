package me.gabcytn.srsly.Auth.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;

import me.gabcytn.srsly.Auth.Entity.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleJwtService {
  @Value("${security.oauth.google.client-id}")
  private String CLIENT_ID;

  public UserDto extractUserDto(String token) {

    GoogleIdTokenVerifier verifier =
        new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(CLIENT_ID))
            .build();

    try {
      GoogleIdToken idToken = verifier.verify(token);

      if (idToken != null) {
        GoogleIdToken.Payload payload = idToken.getPayload();

        String givenName = (String) payload.getOrDefault("given_name", "Unknown");
        String familyName = (String) payload.getOrDefault("family_name", "Unknown");

        return new UserDto(payload.getEmail(), givenName + " " + familyName);
      }

      throw new IllegalArgumentException("Invalid ID Token.");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
