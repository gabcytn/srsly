package me.gabcytn.srsly.Auth.Exception.Handler;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SignatureExceptionHandler {
  @ExceptionHandler(SignatureException.class)
  public ProblemDetail handler() {
    return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "The JWT signature is invalid.");
  }
}
