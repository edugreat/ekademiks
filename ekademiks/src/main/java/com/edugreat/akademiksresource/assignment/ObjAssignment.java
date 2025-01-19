package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table
public class ObjAssignment extends Assignment {
	
	

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
	
	


}
