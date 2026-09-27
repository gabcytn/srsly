package me.gabcytn.srsly.AI.Exception.Handler;

import me.gabcytn.srsly.AI.Exception.AiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AiExceptionHandler {
  @ExceptionHandler(AiException.class)
  public ProblemDetail handle(AiException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
  }
}
