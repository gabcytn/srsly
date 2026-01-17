package me.gabcytn.srsly.Auth.Exception.Handler;

import me.gabcytn.srsly.Auth.Exception.RefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RefreshTokenExceptionHandler {
  @ExceptionHandler(RefreshTokenException.class)
  public ProblemDetail handler(RefreshTokenException e) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
    problemDetail.setProperty("description", "Invalid refresh token.");
    return problemDetail;
  }
}
