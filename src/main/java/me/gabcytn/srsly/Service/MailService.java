package me.gabcytn.srsly.Service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.DTO.UserProblemToSolveCount;
import me.gabcytn.srsly.Repository.SrsProblemRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {
  private final JavaMailSender mailSender;
  private final SrsProblemRepository srsProblemRepository;

  public void sendSimpleMessage(UserProblemToSolveCount dto) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("cayetanogabriel03@gmail.com");
    message.setTo(dto.userEmail());
    message.setSubject("You have " + dto.count() + " problems to review today");
    message.setText(
        "Hi "
            + dto.userEmail()
            + ",\n\n"
            + "You have "
            + dto.count()
            + " problem(s) to review today.\n"
            + "Stay consistent and keep your streak going.\n\n"
            + "— srsly");
    mailSender.send(message);
  }

  @Async
  public void sendMailReminder() {
    LocalDate now = LocalDate.now();
    List<UserProblemToSolveCount> userProblemCount =
        srsProblemRepository.findUserWithToSolveCountByNextAttemptAtLessThanEqual(now);
    userProblemCount.forEach(this::sendSimpleMessage);
  }
}
