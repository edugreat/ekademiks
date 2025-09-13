package com.edugreat.akademiksresource.controller;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AppCustomException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.service.StudentService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/students")
@AllArgsConstructor
//@CrossOrigin("http://localhost:4200")
public class StudentController {

	private final StudentService service;

	@GetMapping("/score")
	public ResponseEntity<Object> getScore(@RequestParam("stud") String stId, @RequestParam("test") String tId) {

		// check if the input path variables match the regular expression for only non
		// white spaced digits
		Pattern pattern = Pattern.compile("^\\d+$");

		Matcher m1 = pattern.matcher(stId);
		Matcher m2 = pattern.matcher(tId);
		if (!(m1.matches() && m2.matches())) {

			throw new AppCustomException("Invalid input '" + stId + "' or '" + tId + "'",
					Exceptions.ILLEGAL_DATA_FIELD.name());
		}

		Integer studentId = Integer.parseInt(stId);
		Integer testId = Integer.parseInt(tId);

		List<ScoreAndDate> response = service.getScore(studentId, testId);

		return ResponseEntity.ok(response);

	}

	@GetMapping("/test")
	public Collection<Question> takeTest(@RequestParam("test_id") String tId) {

		// CHECK TO CONFIRM THE ARGUMENT IS A VALID INTEGER
		Pattern p = Pattern.compile("^\\d+$");
		if (!p.matcher(tId).matches()) {
			throw new AppCustomException("Invalid input " + tId, Exceptions.ILLEGAL_DATA_FIELD.name());
		}

		Integer testId = Integer.parseInt(tId);

		Collection<Question> questions = null;

		questions = service.takeTest(testId);

		return questions;

	}

}
