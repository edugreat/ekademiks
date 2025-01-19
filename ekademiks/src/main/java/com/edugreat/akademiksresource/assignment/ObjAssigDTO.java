package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
import java.util.Set;

public class ObjAssigDTO extends AssignmentDTO {
	
	private Set<String> options = new HashSet<>();

	public Set<String> getOptions() {
		return options;
	}

	public void setOptions(Set<String> options) {
		this.options = options;
	}
	
	


	
}
