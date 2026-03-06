package me.gabcytn.srsly.Exception.Handler;

import me.gabcytn.srsly.Exception.GenericForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenericForbiddenExceptionHandler {
	@ExceptionHandler(GenericForbiddenException.class)
	public ProblemDetail handle(GenericForbiddenException e) {
		return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
	}
}
