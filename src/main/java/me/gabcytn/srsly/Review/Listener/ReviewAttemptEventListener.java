package me.gabcytn.srsly.Review.Listener;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Review.Event.ReviewAttemptEvent;
import me.gabcytn.srsly.Review.Service.ReviewAttemptService;
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
