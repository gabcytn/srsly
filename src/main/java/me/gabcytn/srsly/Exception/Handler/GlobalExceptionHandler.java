package me.gabcytn.srsly.Exception.Handler;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public ProblemDetail problemDetail(Exception e) {
    ProblemDetail errorDetail =
        ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), e.getMessage());
    errorDetail.setProperty("description", "Unknown internal server error.");
    return errorDetail;
  }
}
