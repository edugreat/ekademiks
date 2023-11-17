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
//models the options for question asked in an academic test
public class Option {
	
	@Column(updatable = false)
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for option text is missing")
	private String optionText;//an option which is probably the correct answer
	
	@Enumerated(EnumType.STRING)
	@Column
	@NotNull(message = "Required field for option letter is missing")
	private Options optionLetter;//the option letter(e.g 'A','B','C','D')
	
	@ManyToOne
	@JoinColumn(name = "question_id")
	private Question question;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "option")
	private Set<StudentSelectedOption> studentSelectedOptions;
	
	
	

}
