package com.edugreat.akademiksresource.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.model.StudentTest;

public interface StudentTestDao extends JpaRepository<StudentTest, Integer>{
	
	
	//gets the Student test so we can access their performance on a particular test
	@Query("SELECT st FROM StudentTest st join st.student s join st.tests t WHERE"
			+ " s.id =:studentId AND t.id =:testId")
	public StudentTest findStudentTest(int studentId, int testId);

}
