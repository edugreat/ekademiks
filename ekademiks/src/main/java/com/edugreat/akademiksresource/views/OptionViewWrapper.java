package com.edugreat.akademiksresource.views;

import java.util.List;

import org.springframework.stereotype.Component;

import com.edugreat.akademiksresource.model.Option;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
public class OptionViewWrapper {
	
	@JsonView(OptionView.class)
	private List<Option> options;

}
