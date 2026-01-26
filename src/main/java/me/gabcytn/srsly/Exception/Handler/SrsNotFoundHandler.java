package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.SrsNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SrsNotFoundHandler {
	@ExceptionHandler(SrsNotFound.class)
	public ProblemDetail handler(SrsNotFound e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
	}
}
