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

import com.edugreat.akademiksresource.views.OptionView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import enums.Options;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "`option`")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//models information about the option(likely answer) for a particular question asked in an academic test
public class Option {
	
	@Column(updatable = false)
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(OptionView.class)
	private Integer id;
	
	@Column
	@NotNull(message = "Required field for option text is missing")
	@JsonView(OptionView.class)
	//an option which is probably the correct answer
	private String optionText;
	
	@Enumerated(EnumType.STRING)
	@Column
	@NotNull(message = "Required field for option letter is missing")
	@JsonView(OptionView.class)
	//the option letter(e.g 'A','B','C','D')
	private Options optionLetter;
	
	
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "option_id")
	//uni-bidirectional relationship with the information about the students and their selected options
	private Set<StudentSelectedOption> studentSelectedOption;
	
	
	

}
