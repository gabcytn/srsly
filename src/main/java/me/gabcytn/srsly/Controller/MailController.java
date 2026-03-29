package me.gabcytn.srsly.Controller;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Service.MailReminderService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mails")
public class MailController {
  private final MailReminderService reminderService;

  @PatchMapping("/reminders/subscribe")
  public void subscribe() {
    reminderService.subscribe();
  }

  @PatchMapping("/reminders/unsubscribe")
  public void unsubscribe() {
    reminderService.unsubscribe();
  }
}
