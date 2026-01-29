package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.SolutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SolutionExceptionHandler {
  @ExceptionHandler(SolutionException.class)
  public ProblemDetail handler(SolutionException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
  }
}
