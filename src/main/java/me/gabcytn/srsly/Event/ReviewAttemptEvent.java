package me.gabcytn.srsly.Event;

import me.gabcytn.srsly.Entity.Attempt;
import org.springframework.context.ApplicationEvent;

public class ReviewAttemptEvent extends ApplicationEvent {
  private final Attempt attempt;

  public ReviewAttemptEvent(Object source, Attempt attempt) {
    super(source);
    this.attempt = attempt;
  }

  public Attempt getAttempt() {
    return attempt;
  }
}
