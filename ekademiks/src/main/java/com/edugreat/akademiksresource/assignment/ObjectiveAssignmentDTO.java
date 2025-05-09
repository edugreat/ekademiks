package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectiveAssignmentDTO extends AssignmentResourceDTO {
	

	private String type = "objectives";
	private int _index;
	
	private String problem;
	
	private String answer;
	
	private Set<String> options = new HashSet<>();
	
	
	
	
	public ObjectiveAssignmentDTO() {
	}



	public ObjectiveAssignmentDTO(String problem, String answer, Set<String> options) {
		this.problem = problem;
		this.answer = answer;
		this.options = options;
	}

	

	public Set<String> getOptions() {
		return options;
	}

	public void setOptions(Set<String> options) {
		this.options = options;
	}

	public int get_index() {
		return _index;
	}

	public void set_index(int _index) {
		this._index = _index;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String getType() {
		
		return type;
	}
	
	public void setType(String type) {
		
		this.type = type;
	}
	


	
}
