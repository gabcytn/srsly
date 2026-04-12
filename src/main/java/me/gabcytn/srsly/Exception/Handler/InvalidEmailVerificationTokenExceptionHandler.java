package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.InvalidEmailVerificationTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InvalidEmailVerificationTokenExceptionHandler {
  @ExceptionHandler(InvalidEmailVerificationTokenException.class)
  public ProblemDetail handler(InvalidEmailVerificationTokenException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid token.");
  }
}
