package me.gabcytn.srsly.Listener;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Event.ReviewAttemptEvent;
import me.gabcytn.srsly.Service.ReviewAttemptService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReviewAttemptEventListener implements ApplicationListener<ReviewAttemptEvent> {
  private final ReviewAttemptService reviewAttemptService;

  @Override
  public void onApplicationEvent(ReviewAttemptEvent event) {
    reviewAttemptService.save(event.getAttempt());
  }
}
