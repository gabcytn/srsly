package me.gabcytn.srsly.Exception.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HttpRequestMethodNotSupportedExceptionHandler {
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ProblemDetail handler(HttpRequestMethodNotSupportedException e) {
    return ProblemDetail.forStatus(HttpStatus.METHOD_NOT_ALLOWED);
  }
}
