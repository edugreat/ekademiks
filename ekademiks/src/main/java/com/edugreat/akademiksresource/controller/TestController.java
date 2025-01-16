package com.edugreat.akademiksresource.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.TestInterface;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.projection.TestWrapper;
import com.edugreat.akademiksresource.util.AttemptUtil;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tests")
//@CrossOrigin(origins ="http://localhost:4200", maxAge = 3600)
public class TestController {

	private TestInterface service;

	public TestController(TestInterface testInterface) {

		this.service = testInterface;
	}

	@GetMapping("/{id}")
	// get mapping that serves questions for the given test id
	public ResponseEntity<Object> takeTest(@PathVariable("id") Integer testId) {

		TestWrapper questions = service.takeTest(testId);
		return new ResponseEntity<>(questions, HttpStatus.OK);

	}

	// receives the test attempt for submission
	@PostMapping("/submit")
	public ResponseEntity<Map<String, String>> submitTest(@Valid @RequestBody AttemptUtil attempt) {

		try {

			Map<String, String> response = new HashMap<>();

			String result = service.submitTest(attempt);
			response.put("message", result);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (ConstraintViolationException e) {

			throw new AcademicException("Invalid input detected", Exceptions.ILLEGAL_DATA_FIELD.name());
		}

	}

	// Retrieves from the database, all test names for the given academic level
	@GetMapping("/level")
	public ResponseEntity<Object> getForLevel(@RequestParam String level) {

		return ResponseEntity.ok(service.testSubjectFor(level));
	}

	// controller method to retrieve from the database, all test topics for the
	// given subject and category
	@GetMapping
	public ResponseEntity<Object> getTestTopicsAndDurations(@RequestParam String subject,
			@RequestParam String category, @RequestHeader String studentId) {
		
		Integer _studentId = 0;
		
		if(studentId == null) throw new IllegalArgumentException("request header not found");
		
		_studentId = Integer.parseInt(studentId);

		final String regex = "^[a-zA-Z]+(?: [a-zA-Z]+)*$";
		if (!(Pattern.matches(regex, category) && Pattern.matches(regex, category)))
			throw new AcademicException("Illegal inputs", Exceptions.BAD_REQUEST.toString());

		return ResponseEntity.ok(service.testTopicsAndDurations(subject, category, _studentId));

	}

//		Endpoint that returns both the test name and duration for given test id
	@GetMapping("/info")
	public ResponseEntity<Object> getTopicAndDuration(@RequestParam String testId) {

		return ResponseEntity.ok(service.testTopicAndDuration(Integer.parseInt(testId)));
	}

	@GetMapping("/start")
	public ResponseEntity<Object> commenceTest(@RequestParam String topic, @RequestParam String category) {
		final String regex = "^[a-zA-Z]+(?: [a-zA-Z]+)*$";
		if (!(Pattern.matches(regex, category) && Pattern.matches(regex, category)))
			throw new AcademicException("Illegal inputs", Exceptions.BAD_REQUEST.toString());

		return ResponseEntity.ok(service.takeTest(topic, category));

	}

	@GetMapping("/welcome")
	public ResponseEntity<Object> getWelcome() {

		return new ResponseEntity<>(service.getWelcomeMessages(), HttpStatus.OK);

	}

	@GetMapping("/subject_category")
	public ResponseEntity<Object> getSubjectAndCategory(@RequestParam String testId) {

		return ResponseEntity.ok(service.subjectAndCategory(Integer.parseInt(testId)));
	}

}
