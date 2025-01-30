package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;

@Entity
@DiscriminatorValue("objectives")
public class ObjAssignment extends Assignment {
	


	public ObjAssignment() {
		super();
		
	}

	public ObjAssignment(int _index, String problem, String answer) {
		super(_index, problem, answer);
		
	}

	@ElementCollection
	@CollectionTable(name = "assignment_options",
	joinColumns = @JoinColumn(name = "assignment_id")
			)
	@Column(name = "option")
	private Set<String> options = new HashSet<>();

	public Set<String> getOptions() {
		return options;
	}

	public void setOptions(Set<String> options) {
		this.options = options;
	}

	@Override
	public String getType() {
	
		
		return "objectives";
	}
	
	


}
