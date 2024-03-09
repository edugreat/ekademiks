package com.edugreat.akademiksresource.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.edugreat.akademiksresource.enums.Exceptions;

@ControllerAdvice
public class AcademicExceptionHandler {

	@ExceptionHandler(AcademicException.class)
	private ResponseEntity<String> handleAcademicException(AcademicException ex) {

		String errorCode = ex.getErrorCode();
		if (errorCode.equals(Exceptions.BLANK.name())) {

			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NO_CONTENT);
		} else if (errorCode.equals(Exceptions.STUDENT_NOT_FOUND.name())) {

			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		} else if (errorCode.equals(Exceptions.ILLEGAL_DATA_FIELD.name())) {

			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
		} else if (errorCode.equals(Exceptions.RECORD_NOT_FOUND.name())) {

			return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
		}

		else if(errorCode.equals(Exceptions.TEST_ALREADY_EXISTS.name())){
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.ALREADY_REPORTED);
		} else if(errorCode.equals(Exceptions.BAD_REQUEST.name())) {
			return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	//exception handler is called when method argument such as objects failed for their validation constraints
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
	  
		
		String msg ="";
		
		//get the validation binding result
		BindingResult bindingResult = ex.getBindingResult();
		//get the list of errors due to validation failure
		List<ObjectError> errors = bindingResult.getAllErrors();
		for(ObjectError error: errors) {//extract the default error messages and concatenate them
			msg += error.getDefaultMessage()+", ";
		}
		
	  
		
	    return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
	}

}
