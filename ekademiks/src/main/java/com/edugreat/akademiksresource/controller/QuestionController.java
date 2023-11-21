package com.edugreat.akademiksresource.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.dao.QuestionDao;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.views.QuestionView;
import com.edugreat.akademiksresource.views.QuestionViewWrapper;
import com.fasterxml.jackson.annotation.JsonView;

@RestController
@RequestMapping("/questions")
public class QuestionController {
	
	private QuestionDao dao;
	private QuestionViewWrapper wrapper;

	//@Autowired
	public QuestionController(QuestionDao dao, QuestionViewWrapper wrapper) {
		
		this.dao = dao;
		this.wrapper = wrapper;
	}
	
	//retrieves from the database, all questions having the test id
	@GetMapping("/{id}")
	@JsonView(QuestionView.class)
	public ResponseEntity<Object> getAllQuestionByTestId(@PathVariable("id") int id){
		
		List<Question> qustions = dao.findByTestId(id);
		
		wrapper.setQuestions(qustions);
		
		return new ResponseEntity<Object>(wrapper, HttpStatus.OK);
		
		
	}
	
	@GetMapping("/")
	@JsonView(QuestionView.WithQuestionTextAndNumber.class)
	public ResponseEntity<Object> getAllByTopic(@RequestParam(defaultValue = "algebra") String topic){
		
		List<Question> questions = dao.findByTopic(topic);
		
		wrapper.setQuestions(questions);
		
		return new ResponseEntity<Object>(wrapper, HttpStatus.OK);
	}
	

}
