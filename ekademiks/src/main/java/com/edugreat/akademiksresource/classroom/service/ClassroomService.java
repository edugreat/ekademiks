package com.edugreat.akademiksresource.classroom.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edugreat.akademiksresource.classroom.Classroom;
import com.edugreat.akademiksresource.classroom.ClassroomDTO;
import com.edugreat.akademiksresource.classroom.ClassroomDao;
import com.edugreat.akademiksresource.classroom.EnrollmentRequest;
import com.edugreat.akademiksresource.config.RedisValues;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dao.LevelDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.enums.Roles;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.instructor.InstructorDao;
import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Level;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.util.UtilityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassroomService implements ClassroomInterface {

    private final StudentDao studentDao;
	private final InstructorDao instructorDao;
	private final LevelDao levelDao;
	private final AdminsDao adminDao;
	private final ClassroomDao classroomDao;
	private final InstitutionDao institutionDao;
	private final AppAuthInterface authenticationInterface;
	private final UtilityService utilityService;
	
	 @Qualifier(value = "customStringRedisTemplate")
	private final RedisTemplate<String, String> stringRedisTemplate;

   
	@Override
	@Transactional
	public ClassroomDTO createClassroom(ClassroomDTO classroomDTO) {
		
		try {
			
			
			Institution institution = institutionDao.findById(classroomDTO.institutionId())
					.orElseThrow(() -> new IllegalArgumentException("Institution not found with id: " + classroomDTO.institutionId()));
			
			final String userRole = authenticationInterface.extractUserRole(classroomDTO.primaryInstructorId().toString());
			
			Instructor primaryInstructor = null;
			if(userRole != null) {
				
				if(!userRole.equalsIgnoreCase(Roles.Student.name())) {
					
					
					switch (userRole.toLowerCase()) {
					case "admin":
						throw new IllegalArgumentException("Please login as an instructor to create a classroom");
						
						
						case "superadmin":	
							throw new IllegalArgumentException("Please login as an instructor to create a classroom");
							
							
						
					case "instructor":
						
						Optional<Instructor> instructor = instructorDao.findByInstitutionId(classroomDTO.institutionId(), classroomDTO.primaryInstructorId());
						if(instructor.isEmpty()) {
							throw new IllegalArgumentException("You are not an instructor of this institution");
						}
						
						primaryInstructor = instructor.get();
						
						break;

					default:
						throw new IllegalArgumentException("You are not qualified to create a classroom for this institution");
						
					}
				}
				
				
				
			}
			
			
			
			Level level = levelDao.findById(classroomDTO.levelId())
					.orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + classroomDTO.levelId()));
			
		
		
			Classroom classroom = new Classroom();
			BeanUtils.copyProperties(classroomDTO, classroom);
			
			classroom.setPrimaryInstructor(primaryInstructor);
			classroom.setLevel(level);
			classroom.setInstitution(institution);
			
		Classroom newClassroom = 	classroomDao.save(classroom);
		
		
		
		ClassroomDTO newClassroomDTO = new ClassroomDTO.Builder()
				.id(newClassroom.getId())
				.name(newClassroom.getName())
				.description(newClassroom.getDescription())
				.academicYear(newClassroom.getAcademicYear())
				.section(newClassroom.getSection())
				.primaryInstructorId(newClassroom.getPrimaryInstructor().getId())
				.primaryInstructorName(newClassroom.getPrimaryInstructor().getFirstName() 
						+ " " + newClassroom.getPrimaryInstructor().getLastName())
				.levelId(newClassroom.getLevel().getId())
				.institutionName(newClassroom.getInstitution().getName())
				.build();
		
		return newClassroomDTO;
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
	
		
	}
	
	private void validateUserRole(String userRole) {
		
		if(!List.of("admin", "instructor").contains(userRole.toLowerCase())) {
			
			throw new AcademicException("Login as admin or instructor", HttpStatus.NOT_ACCEPTABLE.name());
		}
		
	}

	@Override
	@Transactional(readOnly=true)
	public Page<ClassroomDTO> getManagedClassrooms(Integer userId, String userRole, int page, int pageSize) {
		
		
		validateUserRole(userRole);
		
		final String ADMIN = Roles.Admin.name();
		final String INSTRUCTOR = Roles.Instructor.name();
		
		Page<Classroom> classrooms = Page.empty();
		try {
		
			
			 if(userRole.equalsIgnoreCase(ADMIN)) {
				 classrooms = classroomDao.getAdminManagedClassrooms(userId, PageRequest.of(page, pageSize));
				
	
				
			}
			else if(userRole.equalsIgnoreCase(INSTRUCTOR)) {
				
				if(classroomDao.isPrimaryInstructor(userId)) {
					 classrooms = classroomDao.findByPrimaryInstructorId(userId,  PageRequest.of(page, pageSize));
					
				}
				
				else  classrooms = classroomDao.findByInstructorInSubject(userId,  PageRequest.of(page, pageSize));
				
									
			}
			
			
		} catch (Exception e) {
			
			throw new AcademicException("Error fetching data", HttpStatus.EXPECTATION_FAILED.name());
		}
		
		return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize) : classrooms.map(this::buildFullClassroomData);
	}
	
	private ClassroomDTO buildFullClassroomData(Classroom clzz) {
		
		final Level l = clzz.getLevel();
		final Instructor i = clzz.getPrimaryInstructor();
		
		Set<StudentDTO> studentDTOs = new HashSet<>();
		List<Student> classroomStudents = getClassroomStudents(clzz.getId());
		
		for(Student student : classroomStudents) {
			
			studentDTOs.add(toStudentDTO(student));
			
			
		}
		
		
		
		return new ClassroomDTO.Builder()
				   .id(clzz.getId())
				   .academicYear(clzz.getAcademicYear())
				   .description(clzz.getDescription())
				   .institutionId(clzz.getInstitution().getId())
				   .institutionName(clzz.getInstitution().getName())
				   .levelId(l.getId())
				   .levelName(l.getCategory().name())
				   .name(clzz.getName())
				   .primaryInstructorId(i.getId())
				   .primaryInstructorName(i.getFirstName()+" "+i.getLastName())
				   .section(clzz.getSection())
				   .studentCount(classroomStudents.size())
				   .students(studentDTOs)
				   .build();
	}
	
	private StudentDTO toStudentDTO(Student student){
		
		StudentDTO dto = new StudentDTO();
		
		BeanUtils.copyProperties(student, dto);
		
		
		return dto;
		
		
	}

	@Override
	@Transactional(readOnly=true)
	public Page<ClassroomDTO> getManagedClassroomsByInstitution(Integer userId, String userRole,
			Integer institutionId, int page, int pageSize) {
		
		if (!institutionDao.existsById(institutionId)) {
	        throw new ResourceNotFoundException("Institution not found");
	    }
		
		validateUserRole(userRole);
		
		Page<Classroom> classrooms = Page.empty();
		try {
			
			if(userRole.equalsIgnoreCase(Roles.Instructor.name())) {
				
				if(classroomDao.isPrimaryInstructor(userId)) {
					
					
					
					 classrooms = classroomDao.findByPrimaryInstructorIdAndInstitutionId(userId, institutionId, PageRequest.of(page, pageSize));
			
				
				}
				
		
				else {
					
					classrooms = classroomDao.findByInstructorInSubjectBy(userId, institutionId, PageRequest.of(page, pageSize));
				}
				
			}else if(userRole.equalsIgnoreCase(Roles.Admin.name())) {
				
				 classrooms = classroomDao.findByInstitutionCreatedByAndInstitutionId(userId, institutionId, PageRequest.of(page, pageSize));
				
				return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize) : classrooms.map(c -> buildFullClassroomData(c));
			}
			
			
		} catch (Exception e) {
			

			throw new AcademicException("Error fetching data", HttpStatus.EXPECTATION_FAILED.name());
		}
		
		
		return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize) : classrooms.map(this::buildFullClassroomData);
	}

	@Override
	@Transactional(readOnly=true)
	public Page<ClassroomDTO> getManagedClassroomsByLevel(Integer userId, String userRole, Integer categoryId, int page, int pageSize) {
		
		
		
		validateUserRole(userRole);
		Page<Classroom> classrooms = Page.empty();
		
		try {
			if(userRole.equalsIgnoreCase(Roles.Instructor.name())) {
				
				final boolean isPrimaryInstructor = classroomDao.isPrimaryInstructor(userId);
				
				if(!isPrimaryInstructor) {
					
					 classrooms = classroomDao.findInstructorInSubjectManagedClassroomsBy(userId, categoryId, PageRequest.of(page, pageSize));
					                            
							            
							
				
				}else if(isPrimaryInstructor) {
					
					classrooms = classroomDao.findByPrimaryInstructorIdAndLevelId(userId, categoryId,PageRequest.of(page, pageSize));
					
					
					
				}
			}else if(userRole.equalsIgnoreCase(Roles.Admin.name())) {
				
				classrooms = classroomDao.findByLevelIdAndInstitutionCreatedBy( categoryId,userId, PageRequest.of(page, pageSize));
				
				
			}
		} catch (Exception e) {
			
			throw new AcademicException("Error fetching data", HttpStatus.EXPECTATION_FAILED.name());
		}
		
		
	
		return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize) : classrooms.map(this::buildFullClassroomData);
	}

	@Override
	@Transactional(readOnly=true)
	public Page<ClassroomDTO> getManagedClassroomsByLevelAndInstitution(Integer userId, String userRole, Integer institutionId,
			Integer categoryId, int page, int pageSize) {
		if (!institutionDao.existsById(institutionId)) {
	        throw new ResourceNotFoundException("Institution not found");
	    }
		
		validateUserRole(userRole);
		
		Page<Classroom> classrooms = Page.empty();
		
		try {
			
			if(userRole.equalsIgnoreCase(Roles.Instructor.name())) {
				
				final boolean isPrimaryInstructor = classroomDao.isPrimaryInstructor(userId, institutionId);
				
				if(!isPrimaryInstructor) {
					
					classrooms = classroomDao.findByInstructorInSubjectBy(userId, institutionId, PageRequest.of(page, pageSize));
					
					
							
					
					
				}else if(isPrimaryInstructor) {
					System.out.println("fetching by institution and level");
					
					classrooms = classroomDao.findByPrimaryInstructorIdAndLevelIdAndInstitutionId(userId, categoryId, institutionId, PageRequest.of(page, pageSize));
					
					
				
				}
			}else if(userRole.equalsIgnoreCase(Roles.Admin.name())) {
				
				classrooms = classroomDao.findByInstitutionCreatedByAndInstitutionIdAndLevelId(userId, categoryId, institutionId, PageRequest.of(page, pageSize));
			
				
				
				
			}
			
		} catch (Exception e) {
			
			throw new AcademicException(e.getMessage(), HttpStatus.EXPECTATION_FAILED.name());
		}
		
		
		return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize) : classrooms.map(this::buildFullClassroomData);
	}
	
	
	@Transactional(readOnly = true)
	private List<Student> getClassroomStudents(Integer classroomId){
		
		return studentDao.findByClassroomId(classroomId);
	}

	@Override
	@Transactional
	public void enrollStudents(EnrollmentRequest enrollmentReq, String role) {
		
		System.out.println("selected institution "+enrollmentReq.institutionId());
		
		

		try {
//			ensure really logged in using the role
			String loggedInRole = stringRedisTemplate.opsForValue().get(RedisValues.CURRENT_ROLE+"::"+enrollmentReq.enrollmentOfficer());
			
			if(loggedInRole == null) throw new IllegalArgumentException("Please login to perform this action");
			
			
			
//			get the classroom the student is being enrolled into
			Classroom classroom = classroomDao.findById(enrollmentReq.classroomId())
					               .orElseThrow(() ->  new IllegalArgumentException("No matching records for the given classroom"));
		
			System.out.println("classroom details: "+classroom.getName()+" institution "+classroom.getInstitution().getName());
			
			if(!classroomDao.isFoundInTheInstitution(classroom.getId(), enrollmentReq.institutionId())){
				
				throw new IllegalArgumentException("Please choose a classroom that is part of the institution");
			}
			Admins admin = null;
			
// Ensure enrollment officer is either classroom primary instructor or ADMIN of the institution
			Optional<Instructor> optionalInstructor = instructorDao.findById(enrollmentReq.enrollmentOfficer());
				if(optionalInstructor.isEmpty()) {
					
					admin  = adminDao.findById(enrollmentReq.enrollmentOfficer())
							 .orElseThrow(() -> new IllegalArgumentException("Enrollment officer not found"));
					
				}
				
							
				if(admin != null  ) {
					
					if(!loggedInRole.equalsIgnoreCase(Roles.Admin.name())) {
						throw new IllegalArgumentException("Operation failed! Please login with the appropriate credentials");
					}
					
					classroomDao.save(performEnrollment(enrollmentReq, classroom, admin.getEmail()));
				
					return;
				}

				if(!loggedInRole.equalsIgnoreCase(Roles.Instructor.name())) {
					throw new IllegalArgumentException("Operation failed! Please login with the appropriate credentials");
				}
				
				classroomDao.save(performEnrollment(enrollmentReq, classroom, optionalInstructor.get().getEmail()));
			
			
		} catch (Exception e) {
			
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
	
	private  Classroom performEnrollment(EnrollmentRequest request, Classroom classroom, String enrollmentOfficerEmail) {
		
		for(Integer studentId: request.studentIds()) {
			
			if(!studentDao.isRegisteredInTheInstitution(studentId, request.institutionId())) {
				
				throw new IllegalArgumentException("Ensure students are part of the institution");
			}
			
			Student student = studentDao.findById(studentId).orElseThrow(() -> new IllegalArgumentException("An enrolling student's record was not found"));
			
			classroom.addStudent(student, enrollmentOfficerEmail);
			
				
			
			
		}
		
		return classroom;
	}
	


}
