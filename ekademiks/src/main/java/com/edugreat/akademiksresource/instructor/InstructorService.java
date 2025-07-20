package com.edugreat.akademiksresource.instructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.StudentRecord;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.util.ValidatorService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InstructorService implements InstructorInterface {

	private final ValidatorService  validatorService;
	
	private final InstitutionDao institutionDao;
	
	private final InstructorDao instructorDao;
	private final StudentDao studentDao;
	
	
	
	
	@Override
	@Transactional
	public void addStudentRecords(List<StudentRecord> studentRecords, Integer instutionId, Integer referee) {
		
		
//		get the institution from the database
		final Institution institution = institutionDao.findById(instutionId).orElseThrow(() -> new IllegalArgumentException("Institution does not exist"));

		 Instructor instructor = instructorDao.findById(referee).orElseThrow(() -> new IllegalArgumentException("You have to register first"));		
		if(!institution.getInstructors().contains(instructor)) {
			
			throw new IllegalArgumentException("Please contact Admin for assistance");
			
			
		}
		
//		get the students already registered
		final List<Student> students = institution.getStudents();

		final List<Student> verifiedRecords = validatorService.verifyStudentRecords(studentRecords);

		for (Student s : verifiedRecords) {

			final boolean successful = institution.addStudent(students, s);
			
			if (!successful)throw new IllegalArgumentException("some records already exist");
			
			instructor.addStudent(s);

		}
		
		
		
//		increment the count of total students added
		Integer currentPopulation = institution.getStudentPopulation();
		
		institution.setStudentPopulation( currentPopulation == null ? studentRecords.size() : currentPopulation+studentRecords.size());
		studentDao.saveAllAndFlush(verifiedRecords);
        instructorDao.saveAndFlush(instructor);
		institutionDao.saveAndFlush(institution);

		
	}

}
