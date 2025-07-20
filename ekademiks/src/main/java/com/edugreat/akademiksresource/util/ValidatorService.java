package com.edugreat.akademiksresource.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.StudentRecord;
import com.edugreat.akademiksresource.model.Student;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class ValidatorService {
	
	
	
	private final Validator validator;
	
	private final   StudentDao studentDao;
	private final  PasswordEncoder passwordEncoder;
	 
	 
	
	 
	 
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
	    
//		processes the given student records against the details in the database. Returns true if successful or false otherwise 
		public final List<Student> verifyStudentRecords(List<StudentRecord> records) {

			List<Student> students = new ArrayList<>();

//			use each information in the record to fetch the student from the database
			for (StudentRecord r : records) {

				Optional<Student> op = studentDao.findByEmail(r.email());

				// throws exception if the student does not exist or the supplied password does
				// not match with the actual password in the database
				if (op.isEmpty() || !(passwordEncoder.matches(r.password(), op.get().getPassword())))
					throw new IllegalArgumentException("wrong email and or password");

				students.add(op.get());

			}
			return students;

		}


}
