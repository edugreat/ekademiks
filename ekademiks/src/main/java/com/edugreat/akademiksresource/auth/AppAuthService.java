package com.edugreat.akademiksresource.auth;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.edugreat.akademiksresource.assessment.response.notification.AssessmentResponseBroadcasterService;
import com.edugreat.akademiksresource.chat.dao.GroupMembersDao;
import com.edugreat.akademiksresource.config.RedisValues;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.controller.StudentRegistrationData;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.InstitutionDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.Roles;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.instructor.Instructor;
import com.edugreat.akademiksresource.instructor.InstructorDTO;
import com.edugreat.akademiksresource.instructor.InstructorDao;
import com.edugreat.akademiksresource.instructor.InstructorRegistrationRequest;
import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.Institution;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.model.UserRoles;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppAuthService implements AppAuthInterface {

  

	private final AdminsDao adminsDao;
	private final GroupMembersDao groupMemberDo;
	private final StudentDao studentDao;
	private final InstructorDao instructorDao;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper mapper;
	private final InstitutionDao institutionDao;
	

	

	
	private final RedisTemplate<String, AppUserDTO> redisTemplate;
	
	 @Qualifier(value = "customStringRedisTemplate")
	private final RedisTemplate<String, String> stringRedisTemplate;



	@Transactional
	@Override
	public int studentSignup(StudentRegistrationData registrationData) {
		try {
			
			// Check the type of user wanting to sign up.
			// if the intending user is a student

			// check if the user already exists in the student table
			final boolean existsAsStudentByEmail = studentDao.existsByEmail((registrationData.email()));

			if (existsAsStudentByEmail) {
				throw new AcademicException("Email already exists", Exceptions.BAD_REQUEST.name());
			}

			// checks if the user already exists in the admin table
			final boolean existsAsAdminByEmail = adminsDao.existsByEmail((registrationData.email()));
			if (existsAsAdminByEmail) {
				throw new AcademicException("Email already exists", Exceptions.BAD_REQUEST.name());
			}

			// check if mobile number exists
			if (registrationData.mobileNumber() != null) {

				final boolean existsAsAdminByMobile = adminsDao.existsByMobile(registrationData.mobileNumber());
				if (existsAsAdminByMobile == true)
					throw new AcademicException("Mobile number already exists", Exceptions.BAD_REQUEST.name());

				final boolean existsAsStudentByMobile = studentDao.existsByMobile(registrationData.mobileNumber());
				if (existsAsStudentByMobile == true)
					throw new AcademicException("Mobile number already exists", Exceptions.BAD_REQUEST.name());
			}

		
			Student student = new Student();
					
					BeanUtils.copyProperties(registrationData, student);
					
					student.setPassword(passwordEncoder.encode(registrationData.password()));
					
					student.addRoles(Set.of("Student"));
					
					studentDao.save(student);
			
		} catch (Exception e) {
			 throw e;
		}

		return HttpStatus.CREATED.value();
	}
	
	

	
	@SuppressWarnings("unchecked")
	private <T extends AppUserDTO> T processBeforLogin(Object obj, String selectedRole){
		
		if(obj instanceof Admins) {
			
			String accessToken  = jwtUtil.generateToken((Admins)obj, selectedRole);
			String refreshToken = jwtUtil.createRefreshToken((Admins)obj, selectedRole);
			
			AdminsDTO dto = new AdminsDTO();
			BeanUtils.copyProperties((Admins)obj, dto);
			dto.setAccessToken(accessToken);
			
			dto.setRefreshToken(refreshToken);
			
			//dto.setRoles(Set.of("SuperAdmin"));
			dto.setRoles(Set.of(selectedRole));		
			
			postLoginCleanup(((Admins)obj).getId());
			
			redisTemplate.opsForValue().set(RedisValues.USER_CACHE+"::"+dto.getId(), (AdminsDTO)dto);
			
			
			return (T)dto;
		}
		
		
		if(obj instanceof Student) {
			

			String accessToken  = jwtUtil.generateToken((Student)obj, selectedRole);
			String refreshToken = jwtUtil.createRefreshToken((Student)obj, selectedRole);
			
			StudentDTO dto = new StudentDTO();
			BeanUtils.copyProperties((Student)obj, dto);
			dto.setAccessToken(accessToken);
			
			dto.setRefreshToken(refreshToken);
			dto.setStatus(((Student)obj).getStatus());
			dto.setIsGroupMember(groupMemberDo.isGroupMember(dto.getId()));
			
			postLoginCleanup(((Student)obj).getId());
			
			redisTemplate.opsForValue().set(RedisValues.USER_CACHE+"::"+dto.getId(), (StudentDTO)dto);
			
			
			return (T)dto;
			
			
		}
		
		if(obj instanceof Instructor) {
			

			
			String accessToken  = jwtUtil.generateToken((Instructor)obj, selectedRole);
			String refreshToken = jwtUtil.createRefreshToken((Instructor)obj, selectedRole);
			
			InstructorDTO dto = new InstructorDTO();
			BeanUtils.copyProperties((Instructor)obj, dto);
			dto.setAccessToken(accessToken);
			
			dto.setRefreshToken(refreshToken);
			
			dto.setRoles(Set.of(selectedRole));
			
			postLoginCleanup(((Instructor)obj).getId());
			
			redisTemplate.opsForValue().set(RedisValues.USER_CACHE+"::"+dto.getId(), (InstructorDTO)dto);
			stringRedisTemplate.opsForValue().set(RedisValues.CURRENT_ROLE+"::"+dto.getId(), selectedRole);			
			
			
			return (T) dto;
			
			
		}
		
		throw new RuntimeException("Unidentifiable user");
		
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public <T extends AppUserDTO> T signIn(AuthenticationRequest request, String selectedRole) {
		
		final String username = request.getEmail();
		
		
	switch (selectedRole.toLowerCase()) {
	case "admin":
		
	case "superadmin":
		Optional<Admins> optionalAdmin = adminsDao.findByEmail(username);
		
		if (optionalAdmin.isPresent() && passwordEncoder.matches(request.getPassword(), optionalAdmin.get().getPassword())) {

			Admins admin = optionalAdmin.get();
			
			
			AdminsDTO dto = processBeforLogin(admin, selectedRole);

			return (T) dto;
		}
		break;
		
	case "student":
		
		Optional<Student> optionalStudent = studentDao.findByEmail(username);
		if (optionalStudent.isPresent() && passwordEncoder.matches(request.getPassword(), optionalStudent.get().getPassword())) {

			Student student = optionalStudent.get();
			
			

//			check if the user's account has yet to be enabled
			if (!student.isAccountEnabled()) {

				throw new DisabledException("Account is disabled !");
			}
//			
			StudentDTO dto = processBeforLogin(student, selectedRole);
			return (T) dto;
		}
		
	case "instructor":
		
		Optional<Instructor> optionalInstructor = instructorDao.findByEmail(username);
		if(optionalInstructor.isPresent() && passwordEncoder.matches(request.getPassword(), optionalInstructor.get().getPassword() ) ) {
			
			
			InstructorDTO dto = processBeforLogin(optionalInstructor.get(), selectedRole);
			
					
			return (T) dto;
		}
		
		
	}
	
	throw new AcademicException(loginErrorMessage(selectedRole.toLowerCase()), HttpStatus.BAD_REQUEST.name());
	}
	
	
	private String loginErrorMessage(String role) {
		
		if(role.equals("admin") || role.equals("superadmin")) return "admin not found!";
		if(role.equals("student")) return "student not found!";
		
		if(role.equals("instructor")) return "instructor not found!";
		
		return "user not found!";
   	}

//	Upon login, tends to remove current user from previous cache before caching again

//	Implementation that generates new token using the refresh token (for user validation) after the expiration of the existing token
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AppUserDTO> T generateNewToken(String refreshToken, HttpServletResponse response)
			throws IOException {
		
		

//		extract username from the token
		var email = jwtUtil.extractUsername(refreshToken);
		var roles = jwtUtil.extractRoles(refreshToken);

		String role = roles.get(0);
		
		
		switch (role.toLowerCase()) {
		case "admin": {

			Optional<Admins> optional = adminsDao.findByEmail(email);
			if (optional.isEmpty()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized!");
				return null;
			}

			Admins admin = optional.get();
			String token = jwtUtil.generateToken(admin, role);
			;
			var dto = mapper.map(admin, AdminsDTO.class);

			dto.setAccessToken(token);

//			re-insert cached object after generating new JWT for the user
			
			redisTemplate.opsForValue().set(RedisValues.USER_CACHE+"::"+dto.getId(), (AdminsDTO)dto);
			return (T) dto;

		}

		case "student": {

			Optional<Student> optional = studentDao.findByEmail(email);

			if (optional.isEmpty()) {

				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized!");
				return null;
			}

			Student student = optional.get();
			String token = jwtUtil.generateToken(student, role);
			StudentDTO dto = mapper.map(student, StudentDTO.class);

			dto.setAccessToken(token);

//			re-insert cached object after generating new JWT for the user
			
			redisTemplate.opsForValue().set(RedisValues.USER_CACHE+"::"+dto.getId(), (StudentDTO) dto);
			
			return (T) dto;

		}
		}

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized!");

		return null;

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AppUserDTO> T getCachedUser(String userId) {
	   
	
		try {
			
			AppUserDTO user = redisTemplate.opsForValue().get(RedisValues.USER_CACHE+"::"+userId);
			
			if(user != null && (user instanceof StudentDTO || user instanceof AdminsDTO || user instanceof InstructorDTO)) return (T)user;

			return null;
			
			
		} catch (Exception e) {
			
		
			
			return null;
		}
	
	}
	
	@Override
	public String extractUserRole(String userId) {
		
		final AppUserDTO user = getCachedUser(userId);
		
		 return user !=null ? jwtUtil.extractRoles(user.getAccessToken()).get(0) : null;
		
		
	}

	@Override
	public <T extends AppUserDTO> void resetCachedUser(T user, String cachingKey) {
		
		
		if(user != null) redisTemplate.opsForValue().set(RedisValues.USER_CACHE+"::"+cachingKey, user);
		
		else throw new IllegalArgumentException("cannot reset null user");
		
		
	}
	
	private void postLoginCleanup(Integer userId) {
		
		redisTemplate.delete(RedisValues.USER_CACHE+"::"+userId);
		redisTemplate.delete(RedisValues.PREVIOUS_CHATS+"::"+userId);
		redisTemplate.delete(RedisValues.MY_GROUP_IDs+"::"+userId);
		redisTemplate.delete(RedisValues.MY_GROUP+"::"+userId);
		redisTemplate.delete(RedisValues.MISCELLANEOUS+"::"+userId);
		
		
	}




	@Override
	@Transactional
	public void instructorSignup(InstructorRegistrationRequest request) {
		
		try {
			
			
			if(instructorDao.isDuplicateAccountCreationAttempt(request.email(), request.institution())) {
				
				
				
				throw new IllegalArgumentException("Account already exists!");
				
			}
			
			if(request.mobileNumber() != null && instructorDao.existsByMobileNumber(request.mobileNumber())) {
				
				throw new IllegalArgumentException("Mobile number: "+request.mobileNumber()+" is already registered.");
			}
				
				
				
			
			
			Institution institution = institutionDao.findById(request.institution() )
					.orElseThrow(() -> new RuntimeException("Institution not found"));
			
			Instructor instructor = new Instructor();
			
			BeanUtils.copyProperties(request, instructor);
			instructor.setPassword(passwordEncoder.encode(instructor.getPassword()));
			instructor.setRoles(Set.of(new UserRoles(Roles.Instructor)));
			
			institution.addInstructor(instructor);	
			
			
			institutionDao.save(institution);
			
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		
		
		}
		
		
		
		
	}
	
	
}




