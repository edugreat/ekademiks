package com.edugreat.akademiksresource.assignment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public  class AssignmentResourceDTO  {
	
	
	private Integer id;
	
    private int _index;
	
	private String problem;
	
	private String answer;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	

	
	private Map<String, String> options = new HashMap<>();

	

	
	
//	method to be overwritten by concrete classes.
	public  String getType() {
		
		return "";
	}

	
	

	

}
