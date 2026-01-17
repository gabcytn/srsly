package me.gabcytn.srsly.Auth.Exception.Handler;

import me.gabcytn.srsly.Auth.Exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotFoundExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ProblemDetail problemDetail(NotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }
}
