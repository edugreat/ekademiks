package com.edugreat.akademiksresource.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Entity
@Table
@Data
@Builder

//models a student who's finished an academic test and information about the informations selected
public class StudentSelectedOption {
	
	@Column
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	//boolean variable that asserts the correctness of the option selected by the student
	@Column
	@NotNull(message = "Required field missing! Can't assert the correctness of selected options")
	private boolean isCorrect;

	@ManyToOne
	@JoinColumn(name = "option_id")
	private Option option;
	
	
	//many-to-one association with the student_test object
	@ManyToOne
	@JoinColumn(name = "student_test_id")
	private StudentTest studentTest;
	
}
