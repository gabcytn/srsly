package me.gabcytn.srsly.Auth.Exception.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AccessDeniedExceptionHandler {
  @ExceptionHandler(AccountStatusException.class)
  public ProblemDetail handler() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Unauthorized access.");
  }
}
