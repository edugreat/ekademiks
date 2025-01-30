package com.edugreat.akademiksresource.assignment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentDTO {
	
	
	private Integer id;
	
	@NotNull
	private int _index;
	@NotNull
	private String problem;
	@NotNull
	private String answer;
	
	private String type;
	
	
	
	public AssignmentDTO() {}

	public AssignmentDTO(Integer id, @NotNull int _index, @NotNull String problem, @NotNull String answer, String type) {
		this.id = id;
		this._index = _index;
		this.problem = problem;
		this.answer = answer;
		this.type = type;
	}

	public AssignmentDTO(@NotNull int _index, @NotNull String problem, @NotNull String answer) {
		this._index = _index;
		this.problem = problem;
		this.answer = answer;
	}
	

	

}
