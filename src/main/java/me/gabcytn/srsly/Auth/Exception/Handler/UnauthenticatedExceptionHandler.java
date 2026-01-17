package me.gabcytn.srsly.Auth.Exception.Handler;

import me.gabcytn.srsly.Auth.Exception.UnauthenticatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UnauthenticatedExceptionHandler {
  @ExceptionHandler(UnauthenticatedException.class)
  public ProblemDetail handler(UnauthenticatedException e) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
    problemDetail.setProperty("description", "Invalid credentials.");
    return problemDetail;
  }
}
