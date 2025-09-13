package com.edugreat.akademiksresource.classroom.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.edugreat.akademiksresource.classroom.ClassroomSubject;
import com.edugreat.akademiksresource.classroom.EnrollmentRequest;
import com.edugreat.akademiksresource.classroom.StudentClassroom;
import com.edugreat.akademiksresource.classroom.StudentClassroom.EnrollmentStatus;
import com.edugreat.akademiksresource.classroom.StudentClassroomDao;
import com.edugreat.akademiksresource.classroom.details.ClassroomFullDetails;
import com.edugreat.akademiksresource.classroom.details.ClassroomSpecificDetails;
import com.edugreat.akademiksresource.classroom.details.ClassroomSummarizedDetails;
import com.edugreat.akademiksresource.classroom.details.InstructorBasicDetails;
import com.edugreat.akademiksresource.classroom.details.StudentBasicDetails;
import com.edugreat.akademiksresource.classroom.details.SubjectBasicDetails;
import com.edugreat.akademiksresource.config.RedisValues;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dao.LevelDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dao.SubjectDao;
import com.edugreat.akademiksresource.dto.ClassroomPrimaryInstructorUpdateDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.enums.Roles;
import com.edugreat.akademiksresource.exception.AppCustomException;
import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.instructor.InstructorDao;
import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Level;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.Subject;
import com.edugreat.akademiksresource.util.EnrollmentResponse;
//import com.edugreat.akademiksresource.util.ClassroomPrimaryInstructorUpdateDTO;
import com.edugreat.akademiksresource.util.SubjectAssignmentRequest;
import com.edugreat.akademiksresource.util.UtilityService;

import jakarta.persistence.EntityNotFoundException;
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
	private final SubjectDao subjectDao;
	private final StudentClassroomDao studentClassroomDao;

	@Qualifier(value = "customStringRedisTemplate")
	private final RedisTemplate<String, String> stringRedisTemplate;

	@Override
	@Transactional
	public ClassroomDTO createClassroom(ClassroomDTO classroomDTO) {

		try {

			Institution institution = institutionDao.findById(classroomDTO.institutionId())
					.orElseThrow(() -> new IllegalArgumentException(
							"Institution not found with id: " + classroomDTO.institutionId()));

			final String userRole = authenticationInterface
					.extractUserRole(classroomDTO.primaryInstructorId().toString());

			Instructor primaryInstructor = null;
			if (userRole != null) {

				if (!userRole.equalsIgnoreCase(Roles.Student.name())) {

					switch (userRole.toLowerCase()) {
					case "admin":
						throw new IllegalArgumentException("Please login as an instructor to create a classroom");

					case "superadmin":
						throw new IllegalArgumentException("Please login as an instructor to create a classroom");

					case "instructor":

						Optional<Instructor> instructor = instructorDao
								.findByInstitutionId(classroomDTO.institutionId(), classroomDTO.primaryInstructorId());
						if (instructor.isEmpty()) {
							throw new IllegalArgumentException("You are not an instructor of this institution");
						}

						primaryInstructor = instructor.get();

						break;

					default:
						throw new IllegalArgumentException(
								"You are not qualified to create a classroom for this institution");

					}
				}

			}
			
//			check if classroom already exists 
			if(classroomDao.existsByInstitutionIdAndAcademicYearAndNameIgnoreCase(classroomDTO.institutionId(), classroomDTO.academicYear(), classroomDTO.name())) {
				throw new DataIntegrityViolationException("classroom"+ classroomDTO.name()+" already exists");
			}

			Level level = levelDao.findById(classroomDTO.levelId()).orElseThrow(
					() -> new IllegalArgumentException("Category not found with id: " + classroomDTO.levelId()));

			Classroom classroom = new Classroom();
			BeanUtils.copyProperties(classroomDTO, classroom);

			classroom.setPrimaryInstructor(primaryInstructor);
			classroom.setLevel(level);
			classroom.setInstitution(institution);

			Classroom newClassroom = classroomDao.save(classroom);

			ClassroomDTO newClassroomDTO = new ClassroomDTO.Builder().id(newClassroom.getId())
					.name(newClassroom.getName()).description(newClassroom.getDescription())
					.academicYear(newClassroom.getAcademicYear()).section(newClassroom.getSection())
					.primaryInstructorId(newClassroom.getPrimaryInstructor().getId())
					.primaryInstructorName(newClassroom.getPrimaryInstructor().getFirstName() + " "
							+ newClassroom.getPrimaryInstructor().getLastName())
					.levelId(newClassroom.getLevel().getId()).institutionName(newClassroom.getInstitution().getName())
					.build();

			return newClassroomDTO;

		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());
		}

	}

	private void validateUserRole(String userRole) {
		
	

		if (!List.of("admin", "instructor").contains(userRole.toLowerCase())) {

			throw new AppCustomException("Login as admin or instructor", HttpStatus.NOT_ACCEPTABLE.name());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public Page<ClassroomDTO> getManagedClassrooms(Integer userId, String userRole, int page, int pageSize) {

		validateUserRole(userRole);

		final String ADMIN = Roles.Admin.name();
		final String INSTRUCTOR = Roles.Instructor.name();

		Page<Classroom> classrooms = Page.empty();
		try {

			if (userRole.equalsIgnoreCase(ADMIN)) {
				classrooms = classroomDao.getAdminManagedClassrooms(userId, PageRequest.of(page, pageSize));

				
				
			} else if (userRole.equalsIgnoreCase(INSTRUCTOR)) {

				if (classroomDao.isPrimaryInstructor(userId)) {
				
					classrooms = classroomDao.findByPrimaryInstructorId(userId, PageRequest.of(page, pageSize));

				}

				else {
					
					classrooms = classroomDao.findByInstructorInSubject(userId, PageRequest.of(page, pageSize));
				}
					

			}

		} catch (Exception e) {

			
			throw new AppCustomException("Error fetching data", HttpStatus.EXPECTATION_FAILED.name());
		}
		
		

		return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize)
				: classrooms.map(this::buildFullClassroomData);
	}

	private ClassroomDTO buildFullClassroomData(Classroom clzz) {

		final Level l = clzz.getLevel();
		final Instructor i = clzz.getPrimaryInstructor();

		Set<StudentDTO> studentDTOs = new HashSet<>();
		List<Student> classroomStudents = getClassroomStudents(clzz.getId());

		for (Student student : classroomStudents) {

			studentDTOs.add(toStudentDTO(student));

		}

		return new ClassroomDTO.Builder().id(clzz.getId()).academicYear(clzz.getAcademicYear())
				.description(clzz.getDescription()).institutionId(clzz.getInstitution().getId())
				.institutionName(clzz.getInstitution().getName()).levelId(l.getId()).levelName(l.getCategory().name())
				.categoryLabel(l.getCategoryLabel())
				.name(clzz.getName()).primaryInstructorId(i.getId())
				.primaryInstructorName(i.getFirstName() + " " + i.getLastName()).section(clzz.getSection())
				.studentCount(classroomStudents.size()).students(studentDTOs).build();
	}

	private StudentDTO toStudentDTO(Student student) {

		StudentDTO dto = new StudentDTO();

		BeanUtils.copyProperties(student, dto);

		return dto;

	}

	@Override
	@Transactional(readOnly = true)
	public Page<ClassroomDTO> getManagedClassroomsByInstitution(Integer userId, String userRole, Integer institutionId,
			int page, int pageSize) {

		if (!institutionDao.existsById(institutionId)) {
			throw new ResourceNotFoundException("Institution not found");
		}

		validateUserRole(userRole);

		Page<Classroom> classrooms = Page.empty();
		try {

			if (userRole.equalsIgnoreCase(Roles.Instructor.name())) {

				if (classroomDao.isPrimaryInstructor(userId)) {

					classrooms = classroomDao.findByPrimaryInstructorIdAndInstitutionId(userId, institutionId,
							PageRequest.of(page, pageSize));

				}

				else {

					classrooms = classroomDao.findByInstructorInSubjectBy(userId, institutionId,
							PageRequest.of(page, pageSize));
				}

			} else if (userRole.equalsIgnoreCase(Roles.Admin.name())) {

				classrooms = classroomDao.findByInstitutionCreatedByAndInstitutionId(userId, institutionId,
						PageRequest.of(page, pageSize));

				return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize)
						: classrooms.map(c -> buildFullClassroomData(c));
			}

		} catch (Exception e) {

			throw new AppCustomException("Error fetching data", HttpStatus.EXPECTATION_FAILED.name());
		}

		return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize)
				: classrooms.map(this::buildFullClassroomData);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ClassroomDTO> getManagedClassroomsByLevel(Integer userId, String userRole, Integer categoryId, int page,
			int pageSize) {

		validateUserRole(userRole);
		Page<Classroom> classrooms = Page.empty();

		try {
			if (userRole.equalsIgnoreCase(Roles.Instructor.name())) {

				final boolean isPrimaryInstructor = classroomDao.isPrimaryInstructor(userId);

				if (!isPrimaryInstructor) {

					classrooms = classroomDao.findInstructorInSubjectManagedClassroomsBy(userId, categoryId,
							PageRequest.of(page, pageSize));

				} else if (isPrimaryInstructor) {

					classrooms = classroomDao.findByPrimaryInstructorIdAndLevelId(userId, categoryId,
							PageRequest.of(page, pageSize));

				}
			} else if (userRole.equalsIgnoreCase(Roles.Admin.name())) {

				classrooms = classroomDao.findByLevelIdAndInstitutionCreatedBy(categoryId, userId,
						PageRequest.of(page, pageSize));

			}
		} catch (Exception e) {

			throw new AppCustomException("Error fetching data", HttpStatus.EXPECTATION_FAILED.name());
		}

		return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize)
				: classrooms.map(this::buildFullClassroomData);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ClassroomDTO> getManagedClassroomsByLevelAndInstitution(Integer userId, String userRole,
			Integer institutionId, Integer categoryId, int page, int pageSize) {
		if (!institutionDao.existsById(institutionId)) {
			throw new ResourceNotFoundException("Institution not found");
		}

		validateUserRole(userRole);

		Page<Classroom> classrooms = Page.empty();

		try {

			if (userRole.equalsIgnoreCase(Roles.Instructor.name())) {

				final boolean isPrimaryInstructor = classroomDao.isPrimaryInstructor(userId, institutionId);

				if (!isPrimaryInstructor) {

					classrooms = classroomDao.findByInstructorInSubjectBy(userId, institutionId,
							PageRequest.of(page, pageSize));

				} else if (isPrimaryInstructor) {
					

					classrooms = classroomDao.findByPrimaryInstructorIdAndLevelIdAndInstitutionId(userId, categoryId,
							institutionId, PageRequest.of(page, pageSize));

				}
			} else if (userRole.equalsIgnoreCase(Roles.Admin.name())) {

				classrooms = classroomDao.findByInstitutionCreatedByAndInstitutionIdAndLevelId(userId, institutionId,
						categoryId, PageRequest.of(page, pageSize));

			}

		} catch (Exception e) {

			throw new AppCustomException(e.getMessage(), HttpStatus.EXPECTATION_FAILED.name());
		}

		return classrooms.isEmpty() ? utilityService.emptyPage(page, pageSize)
				: classrooms.map(this::buildFullClassroomData);
	}

	@Transactional(readOnly = true)
	private List<Student> getClassroomStudents(Integer classroomId) {

		return classroomDao.findById(classroomId).orElseThrow(() -> new EntityNotFoundException("Classroom not found"))
				.getStudentEnrollments().stream().map(StudentClassroom::getStudent).toList();

	}

	@Override
	@Transactional
	public EnrollmentResponse enrollStudents(EnrollmentRequest enrollmentReq, String role) {

		EnrollmentResponse enrollmentResponse = new EnrollmentResponse();
		
		try {
//			ensure really logged in using the role
			String loggedInRole = stringRedisTemplate.opsForValue()
					.get(RedisValues.CURRENT_ROLE + "::" + enrollmentReq.enrollmentOfficer());

			if (loggedInRole == null)
				throw new AppCustomException("Please login to perform this action", HttpStatus.BAD_REQUEST.name());
			
			

//			get the classroom the student is being enrolled into
			Classroom classroom = classroomDao.findById(enrollmentReq.classroomId())
					.orElseThrow(() -> new IllegalArgumentException("No matching records for the given classroom"));

			

			if (!classroomDao.isFoundInTheInstitution(classroom.getId(), enrollmentReq.institutionId())) {

				throw new AppCustomException("Please choose a classroom that is part of the institution", HttpStatus.BAD_REQUEST.name());
			}
			
			
			if(loggedInRole.equalsIgnoreCase(Roles.Admin.name())) {
				
				Admins admin = adminDao.findById(enrollmentReq.enrollmentOfficer()).orElseThrow(() -> new IllegalArgumentException("Enrollment officer not found"));
				synchronized (this) {
					performEnrollment(enrollmentReq, classroom, admin.getEmail(), enrollmentResponse);
					
					final ClassroomDTO updatedClassroom = refreshClassroomDetails(classroom);
					
					enrollmentResponse.setUpdatedClassroomDTO(updatedClassroom);
					
					return enrollmentResponse;
				}
			}else if(loggedInRole.equalsIgnoreCase(Roles.Instructor.name())) {
				
				Instructor instructor = instructorDao.findById(enrollmentReq.enrollmentOfficer())
						                 .orElseThrow(() -> new AppCustomException("Enrollment officer not found", HttpStatus.BAD_REQUEST.name()));
				
				if(!classroom.getPrimaryInstructor().getId().equals(instructor.getId())) {
					throw new AppCustomException("Only primary instructor can enroll students", HttpStatus.BAD_REQUEST.name());
				}
				
				synchronized (this) {
					 	performEnrollment(enrollmentReq, classroom, instructor.getEmail(), enrollmentResponse);
						final ClassroomDTO updatedClassroom = refreshClassroomDetails(classroom);
						
						enrollmentResponse.setUpdatedClassroomDTO(updatedClassroom);
						
						return enrollmentResponse;
				}
				
				
			}
			
			
		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());
		}
		
		
		throw new AppCustomException("Unknown error occured while enrolling students", HttpStatus.BAD_REQUEST.name());
		

	}
	
	private boolean isStudentLegitableToEnrollByStatus(String classroomStatus, String studentStatus) {
		
		System.out.println("classroom status "+classroomStatus+" student status "+studentStatus);
		
		return classroomStatus.equals(studentStatus);
		
		
	}
private void performEnrollment(EnrollmentRequest request,
		Classroom classroom, String enrollmentOfficerEmail, EnrollmentResponse enrollmentResponse) {
    Set<Integer> uniqueStudentIds = new HashSet<>(request.studentIds());
  
   
    
    for (Integer studentId : uniqueStudentIds) {
        if (!studentDao.isRegisteredInTheInstitution(studentId, request.institutionId())) {
            throw new IllegalArgumentException("Ensure students are part of the institution");
        }

        Student student = studentDao.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student record not found"));

        final boolean eligible = isStudentLegitableToEnrollByStatus(classroom.getLevel().getCategoryLabel(), student.getStatus());
        
        if(!eligible) {
        	throw new AppCustomException("Student "+student.getFirstName()+" "+student.getLastName()+" not eligible to enroll into the selected classroom", HttpStatus.BAD_REQUEST.name());
        }
        
       
        
        // Check for existing enrollment with locking
        Optional<StudentClassroom> existingActiveEnrollment = studentClassroomDao
            .findByStudentAndClassroomAndYearAndStatus(
                studentId,
                classroom.getId(),
                classroom.getAcademicYear(),
                EnrollmentStatus.ACTIVE
            );

        if (existingActiveEnrollment.isEmpty()) {
            try {
             StudentClassroom newEnrollment = classroom.addStudent(student, enrollmentOfficerEmail);
           
            		 
             studentClassroomDao.save(newEnrollment);
             
             
            String studentFullName = student.getFirstName()+" "+student.getLastName();
            
            updateEnrollmentResponse(enrollmentResponse, "success", studentFullName);
            
            
             
            } catch (DataIntegrityViolationException e) {
              
               
               String studentFullName = student.getFirstName()+" "+student.getLastName();
             
               updateEnrollmentResponse(enrollmentResponse, "failure", studentFullName);
             
               
              
                
            }
        } else {
            throw new IllegalArgumentException("student "+student.getFirstName()+" "+student.getLastName()+""
            		+ " has active enrollment. Consider completing previous enrollment first");
        }
        
      
        
    }
    
    studentDao.flush();  
}

private void updateEnrollmentResponse(EnrollmentResponse response, String status, String studentFullName) {
	
	switch (status.toLowerCase()) {
	case "failure":
		
	case "success":
		
		
		if(!response.getResponseMap().containsKey(status.toLowerCase())) {
			
			response.getResponseMap().put(status.toLowerCase(), studentFullName);
		}else {
			
			response.getResponseMap().compute(status, (key, value) -> value.concat(", ").concat(studentFullName));
		}
		
		break;

	default:
		throw new AppCustomException("System detected critical error while enrolling students", HttpStatus.BAD_REQUEST.name());
	}
	
	
	
}


	@Override
	@Transactional(readOnly = true)
	public ClassroomFullDetails adminClassroomDetails(Integer classroomId, Integer userId, Integer institutionId) {

		try {
//			confirm the user has administrative role in the given class
			if (!institutionDao.isAdminOfInstitution(institutionId, userId)) {

				throw new IllegalArgumentException("Sorry, your details are unrelated for this operation");
			}

//			confirm the given classroom details exist
			Classroom classroom = classroomDao.findByIdAndInstitutionId(classroomId, institutionId)
					.orElseThrow(() -> new IllegalArgumentException("Not matching classroom details found"));

			return new ClassroomFullDetails(classroom);

		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public ClassroomSpecificDetails subjectInstructorClassroomDetails(Integer classroomId, Integer userId,
			Integer institutionId) {

		try {

//			verify identity
			if (!classroomDao.isSubjectInstructor(userId, institutionId, classroomId)) {

				throw new IllegalArgumentException("No matching record found");
			}

			Classroom classroom = classroomDao.findById(classroomId).get();

			return new ClassroomSpecificDetails(classroom, userId);

		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClassroomSummarizedDetails primaryInstructorClassroomDetails(Integer classroomId, Integer userId,
			Integer institutionId) {

		try {

//		verify identity
			if (!classroomDao.isPrimaryInstructor(userId, institutionId)) {

				throw new IllegalArgumentException("You lack authorizations for this process");
			}

			Classroom classroom = classroomDao.findByIdAndInstitutionId(classroomId, institutionId)
					.orElseThrow(() -> new IllegalArgumentException("No matching classroom details found"));

			return new ClassroomSummarizedDetails(classroom);
		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());

		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClassroomFullDetails searchAdminClassroom(Integer userId, Integer institutionId, String searchQuery) {

		try {
			Classroom classroom = classroomDao
					.findTop1ByInstitutionCreatedByAndInstitutionIdAndNameContaining(userId, institutionId, searchQuery)
					.orElseThrow(() -> new IllegalArgumentException("No matching result found."));

			return new ClassroomFullDetails(classroom);

		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public ClassroomSummarizedDetails searchPrimaryInstructorClassroom(Integer userId, Integer institutionId,
			String searchQuery) {

		try {

			Classroom classroom = classroomDao
					.findTop1ByPrimaryInstructorIdAndInstitutionIdAndNameContaining(userId, institutionId, searchQuery)
					.orElseThrow(() -> new IllegalArgumentException("No matching result found."));

			return new ClassroomSummarizedDetails(classroom);

		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public ClassroomSpecificDetails searchSubjectInstructorClassroom(Integer userId, Integer institutionId,
			String searchQuery) {

		try {

			Classroom classroom = classroomDao
					.findTop1ByClassroomSubjectsInstructorIdAndInstitutionIdAndNameContaining(userId, institutionId,
							searchQuery)
					.orElseThrow(() -> new IllegalArgumentException("No matching result found."));

			return new ClassroomSpecificDetails(classroom, userId);

		} catch (Exception e) {
			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<StudentBasicDetails> getEnrolledStudents(Integer classroomId, Integer institutionId) {

		try {
			Classroom classroom = classroomDao.findByIdAndInstitutionId(classroomId, institutionId)
					.orElseThrow(() -> new IllegalArgumentException("No matching record found"));

			return classroom.getStudentEnrollments().stream().map(StudentClassroom::getStudent)
					.map(StudentBasicDetails::new).toList();
		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());

		}

	}

	@Override
	public List<InstructorBasicDetails> getInstructorsInInstitution(Integer adminId, Integer institutionId) {

		try {

			Institution institution = institutionDao.findByIdAndCreatedBy(institutionId, adminId)
					.orElseThrow(() -> new EntityNotFoundException("No record of institution was found"));

			Set<Instructor> instructors = institution.getInstructors();

			return instructors.stream().map(InstructorBasicDetails::new).toList();

		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());
		}
	}

	@Override
	@Transactional
	public List<SubjectBasicDetails> assignSubjectInstructor(List<SubjectAssignmentRequest> subjectAssignments, Integer classroomId,
			Integer adminId, Integer institutionId) {
		
		

		try {
			
			if(institutionDao.findByIdAndCreatedBy(institutionId, adminId).isEmpty()) {
				System.out.println("institution: "+institutionId+" admin "+adminId);
				throw new EntityNotFoundException("You are not allowed to modify the classroom");
			}

		
			Classroom classroom = classroomDao.findById(classroomId)
					.orElseThrow(() -> new EntityNotFoundException("Classroom does not exist"));
			List<Integer> subjectIds = subjectAssignments.stream().map(SubjectAssignmentRequest::subjectId).toList();
			List<Integer> instructorIds = subjectAssignments.stream().map(SubjectAssignmentRequest::instructorId).toList();
			System.out.println("subject IDs: "+subjectIds.toString());
			System.out.println("instructor IDs: "+instructorIds.toString());

			Map<Integer, Subject> subjects = subjectDao.findAllById(subjectIds).stream()
					.collect(Collectors.toMap(Subject::getId, s -> s));
			
			Map<Integer, Instructor> instructors = instructorDao.findAllById(instructorIds).stream()
					.collect(Collectors.toMap(Instructor::getId, i -> i));

			if (subjects.isEmpty() || instructors.isEmpty()) {
				throw new EntityNotFoundException("Failed to locate classrooms and or instructors");
			}
				

			for (int i = 0; i < subjectIds.size(); i++) {

				Subject subject = subjects.get(subjectIds.get(i));
				Instructor instructor = instructors.get(instructorIds.get(i));
				classroom.assignSubject(subject, instructor);

			}
			
			classroomDao.flush();
			
			
			

		} catch (Exception e) {

			throw new RuntimeException(e.getLocalizedMessage());
		}
		

		return classroomDao.findById(classroomId)
				.orElseThrow(() -> new EntityNotFoundException("Error fetching updated classroom"))
				.getClassroomSubjects()
				.stream()
				.map(this::mapToSubjectBasicDetails)
				.collect(Collectors.toList());
	}
	
	private SubjectBasicDetails mapToSubjectBasicDetails(ClassroomSubject clzz) {
		
		return new SubjectBasicDetails(clzz);
	}
	

	
	public ClassroomDTO refreshClassroomDetails(Classroom classroom) {
		
		try {
			
		//classroom.getStudentEnrollments().clear();
		
		
		classroom =  classroomDao.findById(classroom.getId()).get();
		
		return buildFullClassroomData(classroom);
			
		} catch (Exception e) {
			
			System.out.println(e);
			throw new AppCustomException("Unknow error processing your request", HttpStatus.BAD_REQUEST.name());
		}
		
		
		
		
		
		
	
	
	}
	
	private void verifyUser(int userId, int institutionId, String userRole, int classroomId) {
	//	ensure really logged in using the role
		String loggedInRole = stringRedisTemplate.opsForValue()
				.get(RedisValues.CURRENT_ROLE + "::" + userId);

		if (loggedInRole == null)
			throw new AppCustomException("Please login to perform this action", HttpStatus.BAD_REQUEST.name());
		
		if(loggedInRole.equalsIgnoreCase(Roles.Admin.name())) {
			
			if(!institutionDao.isAdminOfInstitution(institutionId, userId))
				throw new AppCustomException("Request is not allowed", HttpStatus.BAD_REQUEST.name());
			
			
		}else if(loggedInRole.equalsIgnoreCase(Roles.Instructor.name())) {
			
			if(!classroomDao.isPrimaryInstructor(userId)) {
				throw new AppCustomException("Request is not allowed", HttpStatus.BAD_REQUEST.name());
				
			}else if(!classroomDao.isSubjectInstructor(userId, institutionId, classroomId))
				throw new AppCustomException("Request is not allowed", HttpStatus.BAD_REQUEST.name());
		}
		
		
//		get the classroom the student is being enrolled into
		Classroom classroom = classroomDao.findById(classroomId)
				.orElseThrow(() -> new IllegalArgumentException("No matching records for the given classroom"));

		

		if (!classroomDao.isFoundInTheInstitution(classroom.getId(), institutionId)) {

			throw new AppCustomException("Please choose a classroom that is part of the institution", HttpStatus.BAD_REQUEST.name());
		}
		
		
		
	}

	

	@Override
	@Transactional
	public void updatePrimaryInstructor(ClassroomPrimaryInstructorUpdateDTO dto, Integer adminId) {

		try {
			
			if(! institutionDao.isAdminOfInstitution(dto.institutionId(), adminId)) throw new AppCustomException("Please login as admin", HttpStatus.BAD_REQUEST.name());

			Instructor instructor = instructorDao.findById(dto.instructorId())
					.orElseThrow(() -> new EntityNotFoundException("Instructor not found"));
			Classroom classroom = classroomDao.findById(dto.classroomId())
					.orElseThrow(() -> new EntityNotFoundException("Classroom not found"));

//			verify instructor is registered under same institution as the classroom
			Institution institution = classroom.getInstitution();
			if (!institution.getInstructors().contains(instructor)) {
				throw new AppCustomException("Instructor not registered under the intended institution", HttpStatus.BAD_REQUEST.name());
			}

			classroom.setPrimaryInstructor(instructor);
			classroomDao.save(classroom);

		} catch (Exception e) {

			throw new RuntimeException(e);
		}

	}


	
	
	

}