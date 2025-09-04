package com.edugreat.akademiksresource.classroom.details;

import com.edugreat.akademiksresource.model.Level;

public record LevelBasicDetails(
		Integer id,
		String name,
		String label
		) {
	public LevelBasicDetails(Level level) {
		
		this(level.getId(), level.getCategory().name(), level.getCategory().getLabel());
	}

}
