package com.edugreat.akademiksresource.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/*
 * The category class models the category a particular academic subject belongs.
 * For our use case, we make use of categories like JAMB, GCE and WAEC
 */

@Entity
@Table
public class Category {

	//The key used for identification
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	//The name of a particular category such as GCE
	@Column(name = "name")
	private String name;
	
	//The set of Subjects belonging to a particular category
	@JsonManagedReference
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Subject> subjects = new HashSet<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(Set<Subject> subjects) {
		this.subjects = subjects;
	}

	public int getId() {
		return id;
	}
	
	
}
