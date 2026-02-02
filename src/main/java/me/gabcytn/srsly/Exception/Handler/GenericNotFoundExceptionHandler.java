package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.GenericNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenericNotFoundExceptionHandler {
  @ExceptionHandler(GenericNotFoundException.class)
  public ProblemDetail handler(GenericNotFoundException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
  }
}
