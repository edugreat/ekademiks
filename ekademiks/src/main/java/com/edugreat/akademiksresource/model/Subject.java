package com.edugreat.akademiksresource.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/*
 * The subject class models the academic subject taken in the school.
 * The fields provided are the relevant ones for our use case
 */

@Entity
@Table(name = "Subject")
public class Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	// Name of the particular subject
	@Column(name = "name")
	private String name;

	// The question asked in a particular question number
	@Column(name = "question")
	private String question;

	// The answer for a particular question number
	@Column(name = "answer")
	private String answer;

	// The question number
	@Column(name = "question_number")
	private Integer number;

	// The year the particular question was asked
	@Column(name = "exam_year")
	private Date examYear;

	// The category a particular question belong
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "course_category", nullable = false)
	private Category category;

	// The options(available answers to choose from) for a particular question
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "subject")
	private Options options;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Date getExamYear() {
		return examYear;
	}

	public void setExamYear(Date examYear) {
		this.examYear = examYear;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public Integer getId() {
		return id;
	}

}
