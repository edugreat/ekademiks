package com.edugreat.akademiksresource.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.SubjectInterface;
import com.edugreat.akademiksresource.dao.LevelDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.enums.Category;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Level;
import com.edugreat.akademiksresource.model.Subject;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService implements SubjectInterface {
	
	
	private final LevelDao levelDao;
	private final SubjectDao subjectDao;


	@Override
	@Transactional
	public SubjectDTO setSubject(SubjectDTO subjectDTO) {

		// get the academic level for the subject
		Category category = Category.valueOf(subjectDTO.getCategory());
		Level level = levelDao.findByCategory(category);

		if (level == null)
			throw new AcademicException("category, '" + category + "' not found", Exceptions.RECORD_NOT_FOUND.name());
		
		if(subjectDao.subjectExists(subjectDTO.getSubjectName(), level.getId()))
			throw new AcademicException("Subject, "+subjectDTO.getSubjectName()+", exists for the category "+category.name(), Exceptions.BAD_REQUEST.name());
		
		Subject subject = new Subject(subjectDTO.getSubjectName(), level);
		
		level.addSubject(subject);
		subject = subjectDao.save(subject);
		
		return convertToDTO(subject);

	}
	
	private SubjectDTO convertToDTO(Subject subject) {
		
		return new SubjectDTO(subject.getId(), subject.getSubjectName(), subject.getLevel().getCategory().name());
	}



}
