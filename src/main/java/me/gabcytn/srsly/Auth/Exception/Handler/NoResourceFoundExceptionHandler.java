package me.gabcytn.srsly.Auth.Exception.Handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class NoResourceFoundExceptionHandler {
  @ExceptionHandler(NoResourceFoundException.class)
  public ProblemDetail handler() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Path not found.");
  }
}
