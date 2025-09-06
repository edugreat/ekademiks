package com.edugreat.akademiksresource.util;

import java.util.HashMap;
import java.util.Map;

import com.edugreat.akademiksresource.classroom.ClassroomDTO;

public class EnrollmentResponse {
	
	private ClassroomDTO updatedClassroomDTO;
	
	private Map<String, String> responseMap = new HashMap<>();

	public void setUpdatedClassroomDTO(ClassroomDTO updatedClassroomDTO) {
		this.updatedClassroomDTO = updatedClassroomDTO;
	}

	public void setResponseMap(Map<String, String> responseMap) {
		this.responseMap = responseMap;
	}

	public ClassroomDTO getUpdatedClassroomDTO() {
		return updatedClassroomDTO;
	}

	public Map<String, String> getResponseMap() {
		return responseMap;
	}
	
	
	

}
