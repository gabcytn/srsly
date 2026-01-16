package me.gabcytn.srsly.Exception.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MissingRequestCookieExceptionHandler {
  @ExceptionHandler(MissingRequestCookieException.class)
  public ProblemDetail handler(MissingRequestCookieException e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
  }
}
