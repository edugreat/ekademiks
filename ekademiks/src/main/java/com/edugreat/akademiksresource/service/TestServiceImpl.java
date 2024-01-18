package com.edugreat.akademiksresource.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.TestInterface;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Test;


//Test service implementation class that provides implementation for Test interface
@Service
public class TestServiceImpl implements TestInterface{

	private TestDao testDao;
	
	public TestServiceImpl(TestDao testDao) {
		
		this.testDao = testDao;
	}
	
	
	@Override
	public void setTest(Test test) {
		// TODO Auto-generated method stub
		
	}

	//helper method that gets the test for the test id
	private Test getTest(Integer id) {
		
		Optional<Test> optional = testDao.findById(id);
		
		if(optional.isPresent())  return testDao.findById(id).get();
		
		//TODO: Throw exception for non existent object
		
		return null;
	}


	@Override
	public List<Question> getQuestions(Integer testId) {
		List<Question> list = new ArrayList<>();
		//we may need some attributes of this test object in the future(eg test duration etc)
		Test test = getTest(testId);
		
		//TODO: remove the if statement in the future when exception handling for getTest(id) has been implemented
		if(test != null) {
			list = testDao.findQuestionsById(testId);
		}
		
		
		return list;
	}

}
