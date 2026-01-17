package me.gabcytn.srsly.Auth.Exception.Handler;

import me.gabcytn.srsly.Auth.Exception.DuplicateEmailException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DuplicateEmailExceptionHandler {
  @ExceptionHandler(DuplicateEmailException.class)
  public ProblemDetail handler() {
    return ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(409), "Email already taken.");
  }
}
