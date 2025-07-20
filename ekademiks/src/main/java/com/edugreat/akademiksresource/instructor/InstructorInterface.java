package com.edugreat.akademiksresource.instructor;

import java.util.List;

import com.edugreat.akademiksresource.dto.StudentRecord;

public interface InstructorInterface {
	
	
	void addStudentRecords(List<StudentRecord> studentRecords, Integer instutionId, Integer referee);

}
