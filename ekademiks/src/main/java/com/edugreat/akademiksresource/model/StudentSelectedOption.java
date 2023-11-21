package com.edugreat.akademiksresource.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_selected_option")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//models a student who's finished an academic test and the 
//information about the options(likely answers) they selected
public class StudentSelectedOption {
	
	@Column
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	
	@Column
	@NotNull(message = "Required field missing! Can't assert the correctness of selected options")
	//boolean variable that asserts the correctness of the option selected by the student
	private boolean isCorrect;

	
	
	
	
}
