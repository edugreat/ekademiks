package com.edugreat.akademiksresource.service;


import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.LevelInterface;
import com.edugreat.akademiksresource.dao.LevelDao;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Level;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LevelService implements LevelInterface {

	private final LevelDao levelDao;
	private final ModelMapper mapper;

	@Override
	@Transactional
	public LevelDTO addLevel(LevelDTO dto) {
		Level level = null;

		try {
			
			// verifies that the parameter is a valid allowable category. Can throw exception on attempt to provide invalid enum type
				Category.valueOf(dto.getCategory());
				//check if Level object for that category already exists in the database
				if(levelDao.existsByCategory(Category.valueOf(dto.getCategory()))) {
					throw new AcademicException("Record for level '"+dto.getCategory()+"' exists", Exceptions.BAD_REQUEST.name());
					
				}
				 
				level = mapper.map(dto, Level.class);
				

			
			
             //batch persist the levels to the database and map the returned
			//record to the data transfer object
			level = levelDao.save(level);

		} catch (IllegalArgumentException e) {
			throw new AcademicException("Illegal parameter for category", Exceptions.BAD_REQUEST.name());
		}

		return mapper.map(level, LevelDTO.class);
	}

	@Override
	public Iterable<LevelDTO> findAll(){
		
		
		return levelDao.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}
	private LevelDTO convertToDTO(Level level) {

		return mapper.map(level, LevelDTO.class);
	}

}
