package me.gabcytn.srsly.Review.Event;

import me.gabcytn.srsly.Review.Entity.ReviewAttempt;
import org.springframework.context.ApplicationEvent;

public class ReviewAttemptEvent extends ApplicationEvent {
  private final ReviewAttempt reviewAttempt;

  public ReviewAttemptEvent(Object source, ReviewAttempt reviewAttempt) {
    super(source);
    this.reviewAttempt = reviewAttempt;
  }

  public ReviewAttempt getAttempt() {
    return reviewAttempt;
  }
}
