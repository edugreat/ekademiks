package com.edugreat.akademiksresource.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.model.StudentTest;
import com.edugreat.akademiksresource.projection.ScoreAndDate;

public interface StudentTestDao extends JpaRepository<StudentTest, Integer>{
	
	
	
	//get a list of student's score in a particular test.
	//we get list of score since a student can take a particular
	//test multiple times, hence we expect more than one score 
	//for a particular test.
	//using projection, we get both the score and date the test was taken
	@Query("SELECT st.score as score, st.when as when FROM StudentTest st JOIN st.student as s JOIN st.test as t"
			+ " WHERE s.id =:studentId AND t.id =:testId")
	public List<ScoreAndDate> getScore(int studentId, int testId);

	
//	@Query("SELECT st FROM StudentTest st JOIN st.student s WHERE s.id =:studentId AND st.when =:now")
//	public StudentTest findByStudentIdAndWhen(int studentId, LocalDateTime now);

	

}
