package me.gabcytn.srsly.Auth.Exception.Handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ZGlobalExceptionHandler
{
  @ExceptionHandler(Exception.class)
  public ProblemDetail handler(Exception exception) {
    exception.printStackTrace();
    ProblemDetail errorDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    errorDetail.setProperty("exception", exception.getClass().getName());

    return errorDetail;
  }
}
