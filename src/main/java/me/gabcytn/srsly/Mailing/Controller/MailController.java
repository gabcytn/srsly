package me.gabcytn.srsly.Mailing.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Mailing.Service.MailService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Emails")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mails")
public class MailController {
  private final MailService mailService;

  @Operation(summary = "Subscribe to mail reminders")
  @PatchMapping("/reminders/subscribe")
  public void subscribe() {
    mailService.subscribe();
  }

  @Operation(summary = "Unsubscribe to mail reminders")
  @PatchMapping("/reminders/unsubscribe")
  public void unsubscribe() {
    mailService.unsubscribe();
  }
}
