package me.gabcytn.srsly.Exception.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BadCredentialsExceptionHandler {
  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handler(BadCredentialsException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
  }
}
