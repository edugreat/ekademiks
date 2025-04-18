package com.edugreat.akademiksresource.embeddable;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import com.edugreat.akademiksresource.enums.OptionLetter;

@Embeddable
public class Options implements Comparable<Options> {

	@Column(nullable = false)
	private String text;

	@Enumerated(EnumType.STRING)
	@Column(name = "letter", nullable = false)
	private OptionLetter letter;

	public Options() {
	}

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


	@Override
	public boolean equals(Object o) {

		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Options that = (Options) o;

		// When options objects are added to a set, we consider two options having same
		// texts or letters. This helps ensure non repetitive options
		return (text.equals(that.getText()) || letter.equals(that.getLetter()));

	}

	@Override
	public int hashCode() {

		return Objects.hash(letter, text);
	}

//	implements the comparable interface for sorting purpose
	@Override
	public int compareTo(Options that) {
		
		return this.letter.compareTo(that.letter);
	}

}
