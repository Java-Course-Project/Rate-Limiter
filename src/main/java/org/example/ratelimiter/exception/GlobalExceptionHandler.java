package org.example.ratelimiter.exception;

import org.example.ratelimiter.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler({TooManyRequestException.class})
	public ResponseEntity<ErrorResponse> handleTooManyRequestException(TooManyRequestException ex, WebRequest request) {
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
							 .body(ErrorResponse.builder().message(ex.getMessage()).path(request.getContextPath()).build());
	}
}
