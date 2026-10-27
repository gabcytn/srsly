package me.gabcytn.srsly.Settings.Service;

import me.gabcytn.srsly.Auth.Entity.User;
import me.gabcytn.srsly.Auth.Service.UserService;
import me.gabcytn.srsly.Settings.DTO.UserPatchDto;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
  private final UserService userService;

  public SettingsService(UserService userService) {
    this.userService = userService;
  }

  public void patchUser(UserPatchDto userPatchDto) {
    User user = userService.getCurrentUser();
    user.setName(userPatchDto.name());

    userService.save(user);
  }
}
