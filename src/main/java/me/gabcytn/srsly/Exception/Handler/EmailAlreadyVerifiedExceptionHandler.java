package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.EmailAlreadyVerifiedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EmailAlreadyVerifiedExceptionHandler {
  @ExceptionHandler(EmailAlreadyVerifiedException.class)
  public ProblemDetail handler(EmailAlreadyVerifiedException e) {
    return ProblemDetail.forStatusAndDetail(
        HttpStatus.UNPROCESSABLE_ENTITY, "Email is already verified.");
  }
}
