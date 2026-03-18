package me.gabcytn.srsly.Scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.gabcytn.srsly.Service.MailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class MailNotificationScheduler {
	private final MailService mailService;

	@Scheduled(cron = "0 0 7 * * *", zone = "Asia/Manila")
	public void sendEmail() {
		log.info("Sending email to users...");
		mailService.sendMailReminder();
	}
}
