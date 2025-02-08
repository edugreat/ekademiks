package com.edugreat.akademiksresource.assignment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;

@Entity
@DiscriminatorValue("theory")
@AllArgsConstructor
public class Theories extends AssignmentResource {
	

	
	

	public Theories(String answer, int _index, String problem) {
		super(answer, _index, problem);
		
	}

	@Override
	public String getType() {
		
		return "theory";
	}

	
	




	
	
}
