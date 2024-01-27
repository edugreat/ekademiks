package com.edugreat.akademiksresource.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.TestInterface;
import com.edugreat.akademiksresource.dao.StudentTestDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.embeddable.Options;
import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.util.OptionUtil;
import com.edugreat.akademiksresource.util.QuestionUtil;
import com.edugreat.akademiksresource.util.TestUtil;


//Test service implementation class that provides implementation for Test interface
@Service
public class TestServiceImpl implements TestInterface{
	
	private TestDao testDao;
	
	private StudentTestDao studentTestDao;
	
	private SubjectDao subjectDao;
	
	public TestServiceImpl(TestDao testDao, StudentTestDao studentTestDao, SubjectDao subjectDao) {
		
		this.testDao = testDao;
		this.studentTestDao = studentTestDao;
		this.subjectDao = subjectDao;
	}
	
	@Transactional
	@Override
	public void setTest(TestUtil testUtil) {
		
		//create a new Test object and populate its fields from the TestUtil object
		Test test = new Test(testUtil.getTestName(), testUtil.getDuration());
		
		//get the subject associated with this question
		Subject subject = subjectDao.findBySubjectName(testUtil.getSubjectName());
		
		
		//associate the retrieved Subject object to the new created Test object
		subject.addTest(test);
		//get the Questions asked on this test
		List<QuestionUtil> questions = testUtil.getQuestions();
		
		
		for(QuestionUtil q: questions) {
			
			//create a new Question object
			Question question = createQuestion(q);
			
			//add the question to the list of questions for this test
			test.addQuestion(question);
			
			
		}
		
		//save the test
		testDao.save(test);
	}



	//helper method that gets the test for the test id
	private Test getTest(Integer id) {
		
		Optional<Test> optional = testDao.findById(id);
		
		if(optional.isPresent())  return testDao.findById(id).get();
		
		//TODO: Throw exception for non existent object
		
		return null;
	}


	@Override
	public Collection<Question> getQuestions(Integer testId) {
		Collection<Question> list = new ArrayList<>();
		//we may need some attributes of this test object in the future(eg test duration etc)
		Test test = getTest(testId);
		
		//TODO: remove the if statement in the future when exception handling for getTest(id) has been implemented
		if(test != null) {
			list = test.getQuestions();
		}
		
		
		return list;
	}


	@Override
	public List<ScoreAndDate> getScore(int studentId, int testId) {
		
		List<ScoreAndDate> scores = studentTestDao.getScore(studentId, testId);
		
		if(!scores.isEmpty()) {
			
			return scores;
		}
		
		//TODO: Throw exception with descriptive message if list is null to indicate there's not records for the given arguments
		
		return null;
	}
	

	//private helper method that creates and populates new Question object from the given QuestionUtil argument
	private Question createQuestion(QuestionUtil questionUtil) {
	
		//create new Question object
		Question question = new Question(questionUtil.getQuestionNumber(), 
				questionUtil.getText(), questionUtil.getAnswer());
		
		//get the options associated with given QuestionUtil;
		List<OptionUtil> optionUtil = questionUtil.getOptions(); 
		
		//iterate through OptionUtil, creating new Options object
		for(OptionUtil util:optionUtil) {
			Options option = new Options(util.getText(), OptionLetter.valueOf(util.getLetter()));
			
			//populate the options for the newly created Question object
			question.addOption(option);
			
		}
		return question;
	}
	

}
