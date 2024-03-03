package com.edugreat.akademiksresource.contract;

import com.edugreat.akademiksresource.dto.LevelDTO;

public interface LevelInterface {
	
	public LevelDTO addLevel(LevelDTO dto);
	
	public Iterable<LevelDTO> findAll();

}
