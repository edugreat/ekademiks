package com.edugreat.akademiksresource.assignment;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;

@Entity
@DiscriminatorValue("obj")
public class Objectives extends AssignmentResource {
	


	

	@ElementCollection
	@CollectionTable(name = "obj_options")
	@MapKeyColumn(name = "letter")
	@Column(name = "`option`")
	private Map<String, String> options = new HashMap<>();



	public Objectives(String answer, int _index, String problem) {
		super(answer, _index, problem);
		
	}
	
	

	@Override
	public String getType() {
	
		
		return "objectives";
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    Objectives that = (Objectives) o;
	    return super.getId() != null && super.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}

	
	public void addOptions(Map<String, String> options) {
		
		options.forEach((k,v ) -> {
			
			if(this.options.containsValue(v)) throw new IllegalArgumentException("Duplicate options detected!");
			
			this.options.put(k, v);
			
		});
		
	
	}
	


	
}
