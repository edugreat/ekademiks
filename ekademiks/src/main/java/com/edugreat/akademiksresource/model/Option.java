package com.edugreat.akademiksresource.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import enums.Options;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

@Entity
@Table
@Data
@Builder
//models information about the option(likely answer) for a particular question asked in an academic test
public class Option {
	
	@Column(updatable = false)
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for option text is missing")
	//an option which is probably the correct answer
	private String optionText;
	
	@Enumerated(EnumType.STRING)
	@Column
	@NotNull(message = "Required field for option letter is missing")
	//the option letter(e.g 'A','B','C','D')
	private Options optionLetter;
	
	@ManyToOne
	@JoinColumn(name = "question_id")
	//information about the question a particular option is associated to
	private Question question;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "option")
	//bidirectional relationship with the information about the students and their selected options
	private Set<StudentSelectedOption> studentSelectedOptions;
	
	
	

}
