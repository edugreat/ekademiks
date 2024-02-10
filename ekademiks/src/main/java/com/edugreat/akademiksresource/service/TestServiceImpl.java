package com.edugreat.akademiksresource.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.TestInterface;
import com.edugreat.akademiksresource.dao.StudentTestDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dao.TestDao;
import com.edugreat.akademiksresource.embeddable.Options;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Question;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.model.Test;
import com.edugreat.akademiksresource.projection.ScoreAndDate;
import com.edugreat.akademiksresource.projection.TestWrapper;
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
		
		//check if test name exists in the database
		Test t = testDao.findByTestName(testUtil.getTestName());
		if(t  != null) {
			System.out.println("test not null");
			
			throw new AcademicException(t.getTestName()+" already exists", 
					Exceptions.TEST_ALREADY_EXISTS.name());
		}
		
		//create a new Test object and populate its fields from the TestUtil object
		Test test = new Test(testUtil.getTestName(), testUtil.getDuration());
		
		//get the subject associated with this question
		Subject subject = subjectDao.findBySubjectName(testUtil.getSubjectName());
		//throw exception if the subject o which the test is to associated does not exist
		if(subject == null) {
			throw new AcademicException("subject, '"+testUtil.getSubjectName()+ "' does not exist", Exceptions.RECORD_NOT_FOUND.name());
		}
		
		//associate the retrieved Subject object to the newly created Test object
		subject.addTest(test);
		//get the Questions asked on this test
		Set<QuestionUtil> questions = testUtil.getQuestions();
		
		
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
		
		if(!optional.isPresent()) {
			
			throw new AcademicException("Record not found for test id: "+id, Exceptions.RECORD_NOT_FOUND.name());
			
		}
			
			
			return testDao.findById(id).get();
		
		
	}


	@Override
	public TestWrapper getQuestions(Integer testId) {
		TestWrapper wrapper = null;;
		//we may need some attributes of this test object in the future(eg test duration etc)
		Test test = getTest(testId);
		
		 wrapper = new TestWrapper(test.getQuestions());
		
		return wrapper;
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
