package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {
  @Value("${spring.application.frontend.url}")
  private String APP_URL;

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

  @Async
  public void sendVerificationEmail() {
    User user = userService.getCurrentUser();
    if (user.getIsEmailVerified()) {
      throw new EmailAlreadyVerifiedException();
    }
    String verificationToken = jwtService.generateToken(user.getEmail());
    String body =
        "click this link to verify your email: "
            + APP_URL
            + "/verification?token="
            + verificationToken;
    sendSimpleMessage(user.getEmail(), "Email Verification", body);
  }

  public void verifyEmail(String token) {
    String email = userService.getCurrentUserEmail();

    User user = userService.findByEmail(email);
    UserDetails userDetails = new UserPrincipal(user);

    if (!jwtService.isTokenValid(token, userDetails)) {
      throw new InvalidEmailVerificationTokenException();
    }

    user.setIsEmailVerified(Boolean.TRUE);
    userService.save(user);
  }

  @Async
  public void sendMailReminder() {
    LocalDate now = LocalDate.now();
    List<UserProblemToSolveCount> userProblemCount =
        srsProblemRepository.findUserWithToSolveCountByNextAttemptAtLessThanEqual(now);
    userProblemCount.forEach(
        e -> {
          String body =
              "Hi "
                  + e.userEmail()
                  + ",\n\n"
                  + "You have "
                  + e.count()
                  + " problem(s) to review today.\n"
                  + "Stay consistent and keep your streak going.\n\n"
                  + "— srsly";
          String subject = "You have " + e.count() + " problems to review today";
          sendSimpleMessage(e.userEmail(), subject, body);
        });
  }

  private void setUserToReceiveMailReminders(boolean toReceive) {
    User user = userService.getCurrentUser();
    user.setIsSubscribedToMailReminders(toReceive);
    userService.save(user);
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
