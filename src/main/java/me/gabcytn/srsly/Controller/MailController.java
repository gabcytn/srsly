package me.gabcytn.srsly.Controller;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Service.MailReminderService;
import me.gabcytn.srsly.Service.UserService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mails")
public class MailController {
  private final MailReminderService reminderService;
  private final UserService userService;

  @PostMapping("/verification/verify")
  public void verify(@RequestParam(name = "token") String token) {
    userService.verifyEmail(token);
  }

  @PatchMapping("/reminders/subscribe")
  public void subscribe() {
    reminderService.subscribe();
  }

  @PatchMapping("/reminders/unsubscribe")
  public void unsubscribe() {
    reminderService.unsubscribe();
  }
}
