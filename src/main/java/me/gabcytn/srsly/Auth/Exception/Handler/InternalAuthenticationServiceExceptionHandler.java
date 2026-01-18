package me.gabcytn.srsly.Auth.Exception.Handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class InternalAuthenticationServiceExceptionHandler {
	private final BadCredentialsExceptionHandler exceptionHandler;
	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ProblemDetail handler() {
    return exceptionHandler.handler(new BadCredentialsException("Bad credentials"));
	}
}
