package com.example.tenpo.controller.exceptionhandling;

import com.example.tenpo.exception.DataNotFoundException;
import com.example.tenpo.exception.NotUniqueException;
import com.example.tenpo.exception.ServiceUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Date;

@ControllerAdvice
public class RestExceptionHandler
		extends ResponseEntityExceptionHandler {

	@ExceptionHandler({DataNotFoundException.class})
	public ResponseEntity<ErrorDetails> handleDataNotFound(DataNotFoundException ex, WebRequest request) {
		return new ResponseEntity<>(constructError(new Date(), ex.getMessage()), HttpStatus.NOT_FOUND);
	}



	@ExceptionHandler({ NotUniqueException.class})
	public ResponseEntity<ErrorDetails> handleNotUnique(NotUniqueException ex, WebRequest request) {
		return new ResponseEntity<>(constructError(new Date(), ex.getMessage()), HttpStatus.CONFLICT);
	}


	@ExceptionHandler({ ServiceUnavailableException.class})
	public ResponseEntity<ErrorDetails> handleServiceUnavailableException(ServiceUnavailableException ex, WebRequest request) {
		return new ResponseEntity<>(constructError(new Date(), ex.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
	}



	@ExceptionHandler({ BadCredentialsException.class})
	public ResponseEntity<ErrorDetails> handleBadCredentialsDuringLoging(BadCredentialsException ex, WebRequest request) {
		return new ResponseEntity<>(constructError(new Date(), "User or password don´t exist"), HttpStatus.NOT_FOUND);
	}


	//hibernate validator for request params
	@ExceptionHandler({ ConstraintViolationException.class})
	public ResponseEntity<ErrorDetails> handleBadCredentialsDuringLoging(ConstraintViolationException ex, WebRequest request) {
		return new ResponseEntity<>(constructError(new Date(), ex.getMessage()), HttpStatus.BAD_REQUEST);
	}


	//hibernate validator
	@Override protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(constructError(new Date(), ex.getBindingResult().getFieldError().getDefaultMessage()), HttpStatus.BAD_REQUEST);
	}

	private ErrorDetails constructError(Date now, String message) {
		return new ErrorDetails(message, now);
	}

}
