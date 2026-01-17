package me.gabcytn.srsly.Auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class LoginResponseDto {
  private String token;
  private long expiresIn;
}
