package com.edugreat.akademiksresource.assignment;


public class TheoreticalAssigDTO extends AssignmentResourceDTO {
	
	private int _index;
	
	private String problem;
	
	private String answer;
	
	private String type = "theory";

	
	
	
	public TheoreticalAssigDTO() {
	}

	public TheoreticalAssigDTO(String problem, String answer) {
		this.problem = problem;
		this.answer = answer;
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

	
	
	
	
	
	

}
