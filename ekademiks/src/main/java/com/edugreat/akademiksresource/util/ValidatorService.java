package com.edugreat.akademiksresource.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

//@AllArgsConstructor
@RequiredArgsConstructor
@Service
public class ValidatorService {
	
	@Autowired
	 private final Validator validator;
	 
	 
	
	 
	 
	  public <T extends Object> List<String> validateObject(T obj){
	    	
	    	List<String> errors = new ArrayList<>();
	    	
	    	Set<ConstraintViolation<T>> violations = validator.validate(obj);
	    	if(!violations.isEmpty()) {
	    		
	    		violations.stream().map(error -> error.getMessage()).toList() .forEach(e -> errors.add(e));
	    		
	    	}
	    	
	    	return errors;
	    }
	  
	  
	    public <T extends Object> List<String>  validateObjectList(List<T> toValidate) {
	    	
	    	List<String> errors = new ArrayList<>();
	    	
	    	toValidate.forEach(dto -> {
	    		
	    		Set<ConstraintViolation<T>> violations = validator.validate(dto);
	    		
	    		if(!violations.isEmpty()) {
	    			
	    			violations.stream().map(error -> error.getMessage()).toList().forEach(e -> errors.add(e));
	    			
	    		}
	    	});
	    	
	    	return errors;
	    	
	    	
	    }

}
