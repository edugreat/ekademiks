package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
import java.util.Set;

public class ObjectiveAssignmentDTO extends AssignmentResourceDTO {
	

	private int _index;
	
	private String problem;
	
	private String answer;
	
	private Set<String> options = new HashSet<>();

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
		
		return "objectives";
	}
	
	
	


	
}
