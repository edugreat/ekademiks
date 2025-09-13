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

import com.edugreat.akademiksresource.classroom.Classroom;
import com.edugreat.akademiksresource.classroom.ClassroomDao;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.contract.InstitutionInterface;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.exception.AppCustomException;
import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.instructor.InstructorDao;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Student;
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
	private final ClassroomDao classroomDao;
	private final StudentDao studentDao;
	
	
	

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
					
					throw new AppCustomException("Unathorized attempt", HttpStatus.UNAUTHORIZED.name());
					
				}
			}
			

		} catch (Exception e) {
			throw new AppCustomException(e.getMessage(), HttpStatus.BAD_REQUEST.name());
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


	@Override
	@Transactional(readOnly = true)
	public Set<Classroom> getPromotionClassrooms(Integer currentClassroomId, Integer institutionId,
			Integer promotingOfficerId) {
		 
		final String currentUserRole = authenticationInterface.extractUserRole(String.valueOf(promotingOfficerId));
		if(currentUserRole != null) {
			
			Set<Classroom> promotionalClassrooms = new HashSet<>();

			Classroom currentClassroom = classroomDao.findByIdAndInstitutionId(currentClassroomId, institutionId)
					.orElseThrow(() -> new EntityNotFoundException("Select classroom does not exist in the institution"));
			
					
			switch (currentUserRole.toLowerCase()) {
			case "admin":
				
				if(institutionDao.findByIdAndCreatedBy(institutionId, promotingOfficerId).isEmpty()){
					
					throw new EntityNotFoundException("Your record could not be matched with the institution");
				}
				
				break;
				
				
			case "instructor":
				if(! classroomDao.isPrimaryInstructor(promotingOfficerId)) {
					throw new AppCustomException("Verify your are the primary instructor", HttpStatus.BAD_REQUEST.name());
				}
				
				break;
				
				default: throw new AppCustomException("You are not authorized to modify the classroom", HttpStatus.BAD_REQUEST.name());
				
			}
			
			
			List<Classroom> classroomList = classroomDao.getPromotionalClassrooms(currentClassroom.getAcademicYear(), institutionId);
			
			promotionalClassrooms = classroomList.stream()
					                .filter(c -> c.getLevel().getCategory().getHierarchy() > 
					                currentClassroom.getLevel().getCategory().getHierarchy())
					                .collect(Collectors.toSet());
			
			return promotionalClassrooms;
		}
		
		
		
		throw new AppCustomException("Please you could try to login again", HttpStatus.REQUEST_TIMEOUT.name());
	}


	@Override
	@Transactional
	public String promoteStudents(List<Integer> studentIds, 
			                    Integer targetClassroomId, 
			                    Integer currentClassroomId,
			                    Integer promotingOfficerId,
			                    Integer institutionId) {
		
		final String userRole = authenticationInterface.extractUserRole(String.valueOf(promotingOfficerId));
		
		
		if(userRole != null) {
			
			Classroom currentClassroom = null;
			Classroom targetClassroom = null;
			String promotingOfficerEmail = null;
			
			switch (userRole.toLowerCase()) {
			case "admin":
				if(!institutionDao.isAdminOfInstitution(institutionId, promotingOfficerId)) {
					throw new 
					AppCustomException("Your identity could not be match with the selected institution", HttpStatus.BAD_REQUEST.name());
				}
				
				promotingOfficerEmail = adminDao.findById(promotingOfficerId)
						                  .orElseThrow(() -> new AppCustomException("We are unable to fetch your record",HttpStatus.NOT_FOUND.name()))
						                  .getEmail();
				break;
				
				
			case "instructor":
				if(!classroomDao.isPrimaryInstructor(List.of(currentClassroomId, targetClassroomId), promotingOfficerId)) {
					
					throw new AppCustomException("Please contact admin or primary instructor", HttpStatus.BAD_REQUEST.name());
				}
				
				promotingOfficerEmail = instructorDao.findById(promotingOfficerId)
		                  .orElseThrow(() -> new AppCustomException("We are unable to fetch your record",HttpStatus.NOT_FOUND.name()))
		                  .getEmail();
				break;
				

			default: 
				throw new AppCustomException("You are not authorized to modify the classroom", HttpStatus.BAD_REQUEST.name());
				
			}
			
			final List<Classroom> classroomList = classroomDao
					.findByInstitutionCreatedByAndIdIn(promotingOfficerId, List.of(targetClassroomId, currentClassroomId));
					
					currentClassroom = classroomList.stream()
							.filter(c -> c.getId().equals(currentClassroomId))
							.findFirst()
							.orElseThrow(() -> new EntityNotFoundException("Your identity could not be matched with the current classroom"));
					
					
					targetClassroom = classroomList.stream()
							.filter(c -> c.getId().equals(targetClassroomId))
							.findFirst()
							.orElseThrow(() -> new EntityNotFoundException("Your identity could not be matched with the target classroom"));
					
                    //perform students promotion
					Set<Student> students = studentDao.
							            findAllById(studentIds)
							            .stream()
							            .collect(Collectors.toSet());
							
					for(Student student: students) {
						
						currentClassroom.promoteStudent(student, targetClassroom, promotingOfficerEmail);
					}
					
					classroomDao.flush();
					
					return targetClassroom.getName();
		
		}
		

		

		return null;
	}

	

}
