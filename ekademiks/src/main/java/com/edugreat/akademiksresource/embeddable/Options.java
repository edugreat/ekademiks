package com.edugreat.akademiksresource.embeddable;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.annotations.Parent;

import com.edugreat.akademiksresource.enums.OptionLetter;
import com.edugreat.akademiksresource.model.Question;

@Embeddable
public class Options {
	
	@Column(nullable = false)
	private String text;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "letter", nullable = false)
	private OptionLetter letter;
	
	@Parent
	private Question question;
	
	public Options() {}

	public Options(String text, OptionLetter letter) {
		this.text = text;
		this.letter = letter;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public OptionLetter getLetter() {
		return letter;
	}

	public void setLetter(OptionLetter letter) {
		this.letter = letter;
	}

	
	

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	@Override
	public boolean equals(Object o) {
		
		if(this == o) return true;
		if(o == null || getClass()!= o.getClass()) return false;
		
		Options that = (Options)o;
		
		//When options objects are added to a set, we consider two options having same 
		//texts or letters. This helps ensure non repetitive options 
		return (text.equals(that.getText()) || letter.equals(that.getLetter())) 
				&& question.equals(that.getQuestion());
	
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(letter, text);
	}

	
	
	
	
	
	
	
	
	

}
