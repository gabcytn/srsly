package me.gabcytn.srsly.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Auth.DTO.UserPrincipal;
import me.gabcytn.srsly.Auth.Service.EmailJwtService;
import me.gabcytn.srsly.DTO.UserProblemToSolveCount;
import me.gabcytn.srsly.Entity.User;
import me.gabcytn.srsly.Exception.EmailAlreadyVerifiedException;
import me.gabcytn.srsly.Exception.InvalidEmailVerificationTokenException;
import me.gabcytn.srsly.Repository.SrsProblemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {
  @Value("${spring.application.frontend.url}")
  private String APP_URL;

  @Value("${spring.mail.from}")
  private String MAIL_FROM;

  private final TemplateEngine templateEngine;
  private final JavaMailSender mailSender;
  private final SrsProblemRepository srsProblemRepository;
  private final UserService userService;
  private final EmailJwtService jwtService;

  public void subscribe() {
    setUserToReceiveMailReminders(true);
  }

  public void unsubscribe() {
    setUserToReceiveMailReminders(false);
  }

  public void sendVerificationEmail() {
    User user = userService.getCurrentUser();
    if (user.getEmailVerifiedAt() != null) {
      throw new EmailAlreadyVerifiedException();
    }
    String verificationToken = jwtService.generateToken(user.getEmail());
    Context ctx = new Context();
    ctx.setVariable("verificationUrl", APP_URL + "/verification?token=" + verificationToken);
    sendHtmlMessage(user.getEmail(), "Email Verification", "verify-email", ctx);
  }

  public void verifyEmail(String token) {
    String email = userService.getCurrentUserEmail();

    User user = userService.findByEmail(email);
    UserDetails userDetails = new UserPrincipal(user);

    if (!jwtService.isTokenValid(token, userDetails)) {
      throw new InvalidEmailVerificationTokenException();
    }

    user.setEmailVerifiedAt(LocalDateTime.now());
    userService.save(user);
  }

  @Async
  public void sendMailReminder() {
    LocalDate now = LocalDate.now();
    List<UserProblemToSolveCount> userProblemCount =
        srsProblemRepository.findUserWithToSolveCountByNextAttemptAtLessThanEqual(now);
    userProblemCount.forEach(
        e -> {
          Context ctx = new Context();
          ctx.setVariable("count", e.count());
          ctx.setVariable("userEmail", e.userEmail());
          ctx.setVariable("appUrl", APP_URL);
          sendHtmlMessage(
              e.userEmail(),
              "You have " + e.count() + " problems to review today",
              "review-reminder",
              ctx);
        });
  }

  private void setUserToReceiveMailReminders(boolean toReceive) {
    User user = userService.getCurrentUser();
    user.setIsSubscribedToMailReminders(toReceive);
    userService.save(user);
  }

  private void sendHtmlMessage(String to, String subject, String template, Context ctx) {
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(MAIL_FROM);
      helper.setTo(to);
      helper.setSubject(subject);
      String html = templateEngine.process(template, ctx);
      helper.setText(html, true);
      mailSender.send(message);
    } catch (MessagingException e) {
      log.error("MessagingException thrown while sending html emails");
      throw new RuntimeException(e.getMessage());
    }
  }

  private void sendSimpleMessage(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("cayetanogabriel03@gmail.com");
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);
    mailSender.send(message);
  }
}
