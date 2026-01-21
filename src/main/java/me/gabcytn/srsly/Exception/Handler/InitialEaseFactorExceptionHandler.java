package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.InitialEaseFactorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InitialEaseFactorExceptionHandler {
  @ExceptionHandler(InitialEaseFactorException.class)
  public ProblemDetail handle(InitialEaseFactorException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
  }
}
