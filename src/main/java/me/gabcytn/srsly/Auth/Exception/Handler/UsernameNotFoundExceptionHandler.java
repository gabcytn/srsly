package me.gabcytn.srsly.Auth.Exception.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UsernameNotFoundExceptionHandler {
  @ExceptionHandler(UsernameNotFoundException.class)
  public ProblemDetail handler(UsernameNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
  }
}
