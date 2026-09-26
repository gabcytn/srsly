package me.gabcytn.srsly.Review.Publisher;

import lombok.RequiredArgsConstructor;
import me.gabcytn.srsly.Review.Entity.ReviewAttempt;
import me.gabcytn.srsly.Review.Event.ReviewAttemptEvent;
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
