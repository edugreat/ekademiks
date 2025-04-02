package com.edugreat.akademiksresource.assignment;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Entity
@DiscriminatorValue("objectives")
public class Objectives extends AssignmentResource {

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "obj_options")
	@Column(name = "obj_option")
	private Set<String> options = new HashSet<>();

	public Objectives(String answer, int _index, String problem) {
		super(answer, _index, problem);

	}
	
	

	public Objectives() {}



	@Override
	public String getType() {

		return "objectives";
	}

	public Set<String> getOptions() {
		return options;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Objectives that = (Objectives) o;
		return super.getId() != null && super.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

//	ensures non-duplicity of data
	public void addOptions(Set<String> options) {

		for (String option : options) {

			if (this.options.contains(option)) throw new IllegalArgumentException("attempt at having duplicate options");

			this.options.add(option);
		}
	}

}
