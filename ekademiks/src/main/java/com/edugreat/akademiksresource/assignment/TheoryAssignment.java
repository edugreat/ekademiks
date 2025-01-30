package com.edugreat.akademiksresource.assignment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("theory")
public class TheoryAssignment extends Assignment {

	public TheoryAssignment() {
		super();
		
	}

	public TheoryAssignment(int _index, String problem, String answer) {
		super(_index, problem, answer);
		
	}

	@Override
	public String getType() {
		
		return "theory";
	}
	
	
	
	
	
}
