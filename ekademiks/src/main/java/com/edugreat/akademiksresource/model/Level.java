package com.edugreat.akademiksresource.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.edugreat.akademiksresource.enums.Category;

@Entity
@Table
public class Level {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable= false, unique = true)
	@Enumerated(EnumType.STRING)
	private Category category;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "level")
	private Set<Subject> subjects = new HashSet<>();
	
	public Level() {}
	
	public Level(Category category) {
		this.category = category;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Integer getId() {
		return id;
	}

	public Set<Subject> getSubjects() {
		return Collections.unmodifiableSet(subjects);
	}

	public void setSubjects(Set<Subject> subjects) {
		this.subjects = subjects;
	}
	
	public void addSubject(Subject subject) {
		
		this.subjects.add(subject);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		Level that = (Level)obj;
		
		return sameCategory(getCategory(), that.getCategory());
	}
	
	private boolean sameCategory(Category cate1, Category cate2) {
		
		return cate1.name().equals(cate2.name());
	}
	

}
