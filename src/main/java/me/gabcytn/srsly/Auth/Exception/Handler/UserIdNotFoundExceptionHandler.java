package me.gabcytn.srsly.Auth.Exception.Handler;

import me.gabcytn.srsly.Auth.Exception.UserIdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserIdNotFoundExceptionHandler {
  @ExceptionHandler(UserIdNotFoundException.class)
  public ProblemDetail problemDetail(UserIdNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }
}
