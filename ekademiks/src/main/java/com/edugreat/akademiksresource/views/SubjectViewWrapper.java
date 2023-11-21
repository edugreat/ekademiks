package com.edugreat.akademiksresource.views;

import java.util.List;

import com.edugreat.akademiksresource.model.Subject;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

//wrapper for the subject object returned with view
@Setter
@Getter
public class SubjectViewWrapper {
	
	@JsonView(SubjectView.class)
	private List<Subject> subjects;

}
