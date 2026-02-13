package me.gabcytn.srsly.Auth.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class AuthUserDto {
  @NotNull(message = "Email is required.")
  @NotBlank(message = "Email must not be blank.")
  @Email
  private String email;

  @NotNull(message = "Password is required.")
  @NotBlank(message = "Password must not be blank.")
  private String password;

  @NotNull(message = "Device name is required.")
  @NotBlank(message = "Device name must not be blank.")
  private String deviceName;
}
