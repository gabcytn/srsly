package me.gabcytn.srsly.Controller;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Service.MailReminderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/verification")
  public ResponseEntity<Void> verifyEmail() {
    reminderService.sendVerificationEmail();
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping("/verification/verify")
  public void verifyToken(@RequestParam(name = "token") String token) {
    reminderService.verifyEmail(token);
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
