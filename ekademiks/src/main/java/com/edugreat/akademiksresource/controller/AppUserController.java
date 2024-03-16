package com.edugreat.akademiksresource.controller;

import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.AppUserInterface;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/admins")
public class AppUserController {
	
	private final AppUserInterface service;
	
	@GetMapping("/user")
	public ResponseEntity<AppUserDTO> searchByEmail( @RequestParam("email") String email) {
		
		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if(Pattern.matches(emailRegex, email))
		return ResponseEntity.ok(service.searchByEmail(email));
		
		throw new AcademicException("Invalid email format", Exceptions.BAD_REQUEST.name());
		
		
	}
	

	@GetMapping("/students")
	public ResponseEntity<Object> allStudent(){
		
		return ResponseEntity.ok(service.allStudents());
		
		
	}
	
	@GetMapping
	public ResponseEntity<Object> admins(){
		
		return ResponseEntity.ok(service.allAdmins());
	}
	
	@PutMapping
	public ResponseEntity<Object> updatePassword(@Valid AppUserDTO dto) {
		
		service.updatePassword(dto);
		return ResponseEntity.ok("Updated");
		
		
	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteUser(@RequestParam("email")String email){
		
		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if(Pattern.matches(emailRegex, email)) {
			service.deleteUser(email);
			return ResponseEntity.ok("Deleted");
		}
		
		
		throw new AcademicException("Invalid email format", Exceptions.BAD_REQUEST.name());
		
		
	}

}
