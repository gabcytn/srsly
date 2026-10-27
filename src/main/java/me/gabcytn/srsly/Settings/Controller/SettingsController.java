package me.gabcytn.srsly.Settings.Controller;

import jakarta.validation.Valid;
import me.gabcytn.srsly.Settings.DTO.UserPatchDto;
import me.gabcytn.srsly.Settings.Service.SettingsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {
  private final SettingsService settingsService;

  public SettingsController(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @PatchMapping("/user")
  public void patchUserName(@RequestBody @Valid UserPatchDto userPatchDto) {
    settingsService.patchUser(userPatchDto);
  }
}
