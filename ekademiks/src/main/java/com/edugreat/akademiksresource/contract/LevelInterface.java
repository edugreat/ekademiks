package com.edugreat.akademiksresource.contract;

import java.util.Collection;

import com.edugreat.akademiksresource.dto.LevelDTO;

public interface LevelInterface {
	
	public Collection<LevelDTO> addLevel(Collection<LevelDTO> dtos);
	
	public Iterable<LevelDTO> findAll();

}
