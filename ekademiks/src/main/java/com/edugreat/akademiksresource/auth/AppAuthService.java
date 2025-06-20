package com.edugreat.akademiksresource.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edugreat.akademiksresource.chat.dao.GroupMembersDao;
import com.edugreat.akademiksresource.config.RedisValues;
import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.controller.StudentRegistrationData;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.Student;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppAuthService implements AppAuthInterface {

	private final AdminsDao adminsDao;
	private final GroupMembersDao groupMemberDo;
	private final StudentDao studentDao;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper mapper;

	

	
	private final RedisTemplate<String, AppUserDTO> redisTemplate;
	
	

	@Transactional
	@Override
	public int signUp(StudentRegistrationData registrationData) {
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
	@Transactional
	public <T extends AppUserDTO> T signIn(AuthenticationRequest request, String role) {
		
		
		
		String username = request.getEmail();
		String password = request.getPassword();

		// check if the user is an Admin
		if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("superadmin")) {
			Optional<Admins> optionalAdmin = adminsDao.findByEmail(username);
			if (optionalAdmin.isPresent() && passwordEncoder.matches(password, optionalAdmin.get().getPassword())) {

				Admins admin = optionalAdmin.get();
				var accessToken = jwtUtil.generateToken(admin);
				var refreshToken = jwtUtil.createRefreshToken(admin);

				AdminsDTO dto = new AdminsDTO();
				
				BeanUtils.copyProperties(admin, dto);	
				
				dto.setAccessToken(accessToken);
				dto.setRefreshToken(refreshToken);
				
				

//				generate new cache key;

				redisTemplate.opsForValue().set(RedisValues.USER_CACHE+"::"+dto.getId(), (AdminsDTO)dto);

				return (T) dto;
			}

		} else {

			// Then the user might be a student
			Optional<Student> optionalStudent = studentDao.findByEmail(username);
			if (optionalStudent.isPresent() && passwordEncoder.matches(password, optionalStudent.get().getPassword())) {

				Student student = optionalStudent.get();
				
				

//				check if the user's account has yet to be enabled
				if (!student.isAccountEnabled()) {

					throw new DisabledException("Account is disabled !");
				}
//				proceed from here since account is enabled
				var accessToken = jwtUtil.generateToken(student);

				var refreshToken = jwtUtil.createRefreshToken(student);
				StudentDTO dto = new StudentDTO();
				
				BeanUtils.copyProperties(student, dto);
				dto.setAccessToken(accessToken);
				dto.setRefreshToken(refreshToken);

				dto.setStatus(student.getStatus());
				
				dto.setIsGroupMember(groupMemberDo.isGroupMember(dto.getId()));
				
				redisTemplate.opsForValue().set(RedisValues.USER_CACHE+"::"+dto.getId(), (StudentDTO)dto);
				return (T) dto;
			}

		}

		// the user does not exist in the database
		throw new AcademicException(
				(role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("superadmin")) ? "admin not found!"
						: "student not found!",
				Exceptions.RECORD_NOT_FOUND.name());

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
			String token = jwtUtil.generateToken(admin);
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
			String token = jwtUtil.generateToken(student);
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
	public <T extends AppUserDTO> T getCachedUser(String cachingKey) {
	   
	
		try {
			
			AppUserDTO user = redisTemplate.opsForValue().get(RedisValues.USER_CACHE+"::"+cachingKey);
			
			if(user != null && (user instanceof StudentDTO || user instanceof AdminsDTO)) return (T)user;

			return null;
			
			
		} catch (Exception e) {
			
			System.out.println(e);
			
			return null;
		}
	
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
	
	
}




