package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.InitialSolutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InitialSolutionExceptionHandler
{
  @ExceptionHandler(InitialSolutionException.class)
  public ProblemDetail handle(InitialSolutionException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
  }
}
