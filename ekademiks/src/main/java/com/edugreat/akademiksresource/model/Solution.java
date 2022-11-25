package com.edugreat.akademiksresource.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*
 * The solution class models the solution provided to help
 * students master how to tackle a problems encountered in a academic question
 */
@Entity
@Table(name= "Solution")
public class Solution {
	
	//Key used for identification
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	//The name of subject whose solutions have been provided
	@Column(name = "subject")
	private String subject;
	
	//The year the question was asked
	@Column(name = "exam_year")
	private Date examYear;
	
	// link pointing to the solution for the question
	@Column(name = "link")
	private String link;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getExamYear() {
		return examYear;
	}

	public void setExamYear(Date date) {
		this.examYear = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getId() {
		return id;
	}
	
	

}
