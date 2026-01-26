package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.EarlyReviewException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EarlyReviewExceptionHandler {
  @ExceptionHandler(EarlyReviewException.class)
  public ProblemDetail handler() {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.UNPROCESSABLE_ENTITY, "Problem is not supposed to be attempted today.");
  }
}
