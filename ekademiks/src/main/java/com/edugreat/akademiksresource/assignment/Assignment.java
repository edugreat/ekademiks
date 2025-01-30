package com.edugreat.akademiksresource.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

// Base class of theory and PDF based assignments

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Data
public abstract class Assignment {
	
	
	@Column
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
//	represents the question number
	@Column(nullable = false)
	private int _index;
	
	@Column(nullable = false)
	private String problem;
	
	@Column(nullable = false)
	private String answer;
	
	public Assignment() {}

	public Assignment(int _index, String problem, String answer) {
		this._index = _index;
		this.problem = problem;
		this.answer = answer;
	}
	
	
	public abstract String getType();
	
	
	

}
