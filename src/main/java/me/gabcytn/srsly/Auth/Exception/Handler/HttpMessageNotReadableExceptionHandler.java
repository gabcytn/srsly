package me.gabcytn.srsly.Auth.Exception.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HttpMessageNotReadableExceptionHandler {
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handler(HttpMessageNotReadableException e) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    problemDetail.setProperty("description", "Missing required request body.");
    return problemDetail;
  }
}
