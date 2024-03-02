package com.edugreat.akademiksresource.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale.Category;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.LevelInterface;
import com.edugreat.akademiksresource.dao.LevelDao;
import com.edugreat.akademiksresource.dto.LevelDTO;
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
	public List<LevelDTO> addLevel(Collection<LevelDTO> dtos) {
		Set<Level> levels = new HashSet<>();
		List<LevelDTO> savedLevels = null;

		try {
			for (LevelDTO dto : dtos) {
				Category.valueOf(dto.getCategory());// verifies that the parameter is a valid allowable category. Can throw exception on attempt to provide invalid enum type

				Level level = mapper.map(dto, Level.class);
				levels.add(level);

			}
			
             //batch persist the levels to the database and map the returned
			//record to the data transfer object
			savedLevels = levelDao.saveAll(levels).stream().map(this::convertToDTO).collect(Collectors.toList());

		} catch (IllegalArgumentException e) {
			throw new AcademicException("Illegal parameter for category", Exceptions.BAD_REQUEST.name());
		}

		return savedLevels;
	}

	@Override
	public Iterable<LevelDTO> findAll(){
		
		
		return levelDao.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}
	private LevelDTO convertToDTO(Level level) {

		return mapper.map(level, LevelDTO.class);
	}

}
