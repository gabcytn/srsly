package me.gabcytn.srsly.DTO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequestDto {
  @NotNull(message = "Device name is required.")
  @NotBlank(message = "Device name field must not be blank.")
  private String deviceName;

  @JsonCreator
  public RefreshTokenRequestDto(@JsonProperty("deviceName") String deviceName) {
    this.deviceName = deviceName;
  }
}
