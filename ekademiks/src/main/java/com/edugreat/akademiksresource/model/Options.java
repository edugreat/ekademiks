package com.edugreat.akademiksresource.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
/*
 * This class models the Options(available answers to choose from),
 * for a particular academic Subject
 */

@Entity
@Table(name = "Options")
public class Options {
	//Key used for identification
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	//option A
	@Column(name = "a")
	private String a;
	
	//option B
	@Column(name = "b")
	private String b;

	//option C
	@Column(name = "c")
	private String c;

	//option D
	@Column(name = "d")
	private String d;

	//option E
	@Column(name = "e")
	private String e;

	//The subject to which the options belong
	@OneToOne
	@JoinColumn(name = "subject_id")
	private Subject subject;

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public String getE() {
		return e;
	}

	public void setE(String e) {
		this.e = e;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public int getId() {
		return id;
	}
	
	

}
