package io.CarbookingService.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import io.CarbookingService.exceptionhandler.DataNotAllowedException;
import io.CarbookingService.exceptionhandler.DataNotFoundException;
import io.CarbookingService.exceptionhandler.DuplicateDataNotAllowedException;
import io.CarbookingService.exceptionhandler.NullFormatException;
import io.CarbookingService.model.ErrorModel;

@ControllerAdvice
public class BookingErrorControllerAdvice extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity<ErrorModel> mapException(DataNotFoundException e) {
		ErrorModel errorObj=new ErrorModel(HttpStatus.NOT_FOUND.value(),e.getMessage());
		return new ResponseEntity<>(errorObj,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(DuplicateDataNotAllowedException.class)
	public ResponseEntity<ErrorModel> mapException(DuplicateDataNotAllowedException e) {
		ErrorModel errorObj=new ErrorModel(HttpStatus.CONFLICT.value(),e.getMessage());
		return new ResponseEntity<>(errorObj,HttpStatus.CONFLICT);
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorModel errorObj=new ErrorModel(HttpStatus.METHOD_NOT_ALLOWED.value(),ex.getMessage());
		return new ResponseEntity<>(errorObj,HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	@ExceptionHandler(NullFormatException.class)
	public ResponseEntity<ErrorModel> mapException(NullFormatException e) {
		ErrorModel errorObj=new ErrorModel(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
		return new ResponseEntity<>(errorObj,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorModel errorObj=new ErrorModel(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
		return new ResponseEntity<>(errorObj,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataNotAllowedException.class)
	public ResponseEntity<ErrorModel> mapException(DataNotAllowedException e) {
		ErrorModel errorObj=new ErrorModel(HttpStatus.NOT_ACCEPTABLE.value(),e.getMessage());
		return new ResponseEntity<>(errorObj,HttpStatus.NOT_ACCEPTABLE);
	}

}
