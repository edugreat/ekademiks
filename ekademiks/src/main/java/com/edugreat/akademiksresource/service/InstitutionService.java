package com.edugreat.akademiksresource.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.contract.InstitutionInterface;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.instructor.InstructorDao;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Subject;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstitutionService implements InstitutionInterface {
	private final AppAuthInterface authenticationInterface;
	private final InstitutionDao institutionDao;
	private final AdminsDao adminDao;
	private final InstructorDao instructorDao;
	private final SubjectDao subjectDao;
	
	
	

	@Override
	@Transactional(readOnly = true)
	public List<SubjectDTO> findSubjectsForCategory(Integer adminOrInstructorId, Integer categoryId) {
		
				
		
		try {
			final String userRole = authenticationInterface.extractUserRole(String.valueOf(adminOrInstructorId));
			
			if(userRole != null ) {
				
				switch (userRole.toLowerCase()) {
				case "admin":
					
				  if(adminDao.findById(adminOrInstructorId).isEmpty()) {
					  throw new  EntityNotFoundException("User Not Found");
				  }
					Set<Integer> institutionIds = institutionDao.findByCreatedBy(adminOrInstructorId)
							                      .stream()
							                      .map(Institution::getId)
							                      .collect(Collectors.toSet());
					
					return getSubjectsOfferedInInstitutions(institutionIds, categoryId);
					
					
					
					
					
					
				case "instructor":
					
					final Instructor instructor = instructorDao.findById(adminOrInstructorId)
					                              .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
					
					@SuppressWarnings("unused") Set<Integer> instutitionIds = new HashSet<>();
					institutionIds = instructor.getInstitutions()
		                      .stream()
		                      .map(Institution::getId)
		                      .collect(Collectors.toSet());

					
					
					
					
					return getSubjectsOfferedInInstitutions(institutionIds, categoryId );
					
					

				default:
					
					throw new AcademicException("Unathorized attempt", HttpStatus.UNAUTHORIZED.name());
					
				}
			}
			

		} catch (Exception e) {
			throw new AcademicException(e.getMessage(), HttpStatus.BAD_REQUEST.name());
		}
		
		
		return Collections.emptyList();
		
	}

	
	private  List<SubjectDTO>  getSubjectsOfferedInInstitutions(Set<Integer> institutionIds, Integer categoryId) {
		
		 List<SubjectDTO> dtos = new ArrayList<>();
		List<Subject> subjects =  subjectDao.findByInstitutionIds(institutionIds, categoryId);
		for(Subject s : subjects) {
			SubjectDTO dto = new SubjectDTO();
			dto.setId(s.getId());
			dto.setCategory(s.getLevel().getCategoryLabel());
			dto.setSubjectName(s.getSubjectName());
			dtos.add(dto);
		}
		
		return dtos;
	}
	

}
