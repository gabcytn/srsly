package me.gabcytn.srsly.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Emails")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mails")
public class MailController {
  private final MailService mailService;

  @Operation(summary = "Send verification in email")
  @PostMapping("/verification")
  public ResponseEntity<Void> verifyEmail() {
    mailService.sendVerificationEmail();
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @Operation(summary = "Verify email with token")
  @PostMapping("/verification/verify")
  public void verifyToken(@RequestParam(name = "token") String token) {
    mailService.verifyEmail(token);
  }

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
