package me.gabcytn.srsly.Settings.Controller;

import jakarta.validation.Valid;
import me.gabcytn.srsly.Auth.Service.AuthService;
import me.gabcytn.srsly.Settings.DTO.UserPatchDto;
import me.gabcytn.srsly.Settings.DTO.UserSettingsDto;
import me.gabcytn.srsly.Settings.Service.SettingsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {
  private final SettingsService settingsService;
  private final AuthService authService;

  public SettingsController(SettingsService settingsService, AuthService authService) {
    this.settingsService = settingsService;
    this.authService = authService;
  }

  @GetMapping("/")
  public UserSettingsDto settings() {
    return settingsService.getSettingsInfo();
  }

  @PatchMapping("/user")
  public void patchUserName(@RequestBody @Valid UserPatchDto userPatchDto) {
    settingsService.patchUser(userPatchDto);
  }

  @DeleteMapping("/user")
  public void delete(
      @CookieValue(value = "X-REFRESH-TOKEN", required = false) String refreshToken) {
    settingsService.deleteUser();

    if (refreshToken != null) {
      authService.invalidateRefreshToken(refreshToken);
    }
  }
}
