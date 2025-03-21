package com.edugreat.akademiksresource.assignment;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// define some JSON configurations to help determine the concrete class the base class is to be serialized to
@JsonTypeInfo(

		use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({

		@JsonSubTypes.Type(value = ObjectiveAssignmentDTO.class, name = "objectives"),
		@JsonSubTypes.Type(value = TheoreticalAssigDTO.class, name = "theory")

})
public abstract class AssignmentResourceDTO  {
	
	
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	
	public abstract String getType();
 
	

	

}
