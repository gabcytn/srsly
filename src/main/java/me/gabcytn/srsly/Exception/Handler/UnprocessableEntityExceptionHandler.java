package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.UnprocessableEntityException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UnprocessableEntityExceptionHandler {
  @ExceptionHandler(UnprocessableEntityException.class)
  public ProblemDetail handle(UnprocessableEntityException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
  }
}
