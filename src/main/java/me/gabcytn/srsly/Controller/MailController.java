package me.gabcytn.srsly.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Service.MailReminderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mails")
public class MailController {
  private final MailReminderService reminderService;

  @PostMapping("/reminders/subscribe")
  public void subscribe() {
    reminderService.subscribe();
  }

  @PostMapping("/reminders/unsubscribe")
  public void unsubscribe() {
    reminderService.unsubscribe();
  }
}
