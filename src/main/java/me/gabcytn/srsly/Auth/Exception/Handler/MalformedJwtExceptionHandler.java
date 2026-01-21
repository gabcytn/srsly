package me.gabcytn.srsly.Auth.Exception.Handler;

import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MalformedJwtExceptionHandler {
  @ExceptionHandler(MalformedJwtException.class)
  public ProblemDetail handler() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Incorrect token format.");
  }
}
