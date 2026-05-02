package me.gabcytn.srsly.Publisher;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Entity.ReviewAttempt;
import me.gabcytn.srsly.Event.ReviewAttemptEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ReviewAttemptEventPublisher {
  private final ApplicationEventPublisher applicationEventPublisher;

  public void publish(ReviewAttempt reviewAttempt) {
    applicationEventPublisher.publishEvent(new ReviewAttemptEvent(this, reviewAttempt));
  }
}
