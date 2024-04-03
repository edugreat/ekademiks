package com.edugreat.akademiksresource.controller;

import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.AdminInterface;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;

/*
 * Only users with the Admin role can call end-points declared in this class
 */
@RestController
@AllArgsConstructor
@RequestMapping("/admins")
public class AdminController {

	private final AdminInterface service;

	@GetMapping("/user")
	@JsonView(UserView.class)
	public ResponseEntity<AppUserDTO> searchByEmail(@RequestParam("email") String email) {

		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (Pattern.matches(emailRegex, email))
			return ResponseEntity.ok(service.searchByEmail(email));

		throw new AcademicException("Invalid email format", Exceptions.BAD_REQUEST.name());

	}

	@GetMapping("/students")
	@JsonView(UserView.class)
	public ResponseEntity<List<StudentDTO>> allStudent() {

		return ResponseEntity.ok(service.allStudents());

	}

	@GetMapping
	public ResponseEntity<Object> admins() {

		return ResponseEntity.ok(service.allAdmins());
	}

	@PutMapping
	public ResponseEntity<Object> updatePassword(@RequestBody @Valid AppUserDTO dto) {

		service.updatePassword(dto);
		return ResponseEntity.ok("Updated");

	}

	@DeleteMapping
	public ResponseEntity<String> deleteUser(@RequestParam("email") String email) {

		final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (Pattern.matches(emailRegex, email)) {
			service.deleteUser(email);
			return ResponseEntity.ok("Deleted");
		}

		throw new AcademicException("Invalid email format", Exceptions.BAD_REQUEST.name());

	}

	@PostMapping("/sub")
	public ResponseEntity<Object> setSubject(@RequestBody @Valid SubjectDTO dto) {

		return new ResponseEntity<>(service.setSubject(dto), HttpStatus.CREATED);
	}

	// set a new academic test
	@PostMapping("/test")
	public ResponseEntity<Object> setTest(@RequestBody @Valid TestDTO testDTO) {

		service.setTest(testDTO);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	@GetMapping("levels")
	public ResponseEntity<Object> findAll(){
		
		return new ResponseEntity<Object>(service.findAllLevels(), HttpStatus.OK);
	}
	
	@PostMapping("/level")
	public ResponseEntity<Object> addLevel(@RequestBody  @Valid LevelDTO dto) {
		
		return new ResponseEntity<>(service.addLevel(dto), HttpStatus.OK);
		
	}
	

}
