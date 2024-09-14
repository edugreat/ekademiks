package com.edugreat.akademiksresource.auth;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.AppAuthInterface;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.enums.Roles;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.AppUser;
import com.edugreat.akademiksresource.model.Student;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppAuthService implements AppAuthInterface {

	private final AdminsDao adminsDao;
	private final StudentDao studentDao;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper mapper;

	@Transactional
	@Override
	public int signUp(AppUserDTO userDTO) {
		AppUserDTO user = null;
		AppUser appUser = null;
		// Check the type of user wanting to sign up.
		// if the intending user is a student

		// check if the user already exists in the student table
		final boolean existsAsStudentByEmail = studentDao.existsByEmail((userDTO.getEmail()));

		if (existsAsStudentByEmail) {
			throw new AcademicException("Email already exists", Exceptions.BAD_REQUEST.name());
		}

		// checks if the user already exists in the admin table
		final boolean existsAsAdminByEmail = adminsDao.existsByEmail((userDTO.getEmail()));
		if (existsAsAdminByEmail) {
			throw new AcademicException("Email already exists", Exceptions.BAD_REQUEST.name());
		}

		// check if mobile number exists
		if (userDTO.getMobileNumber() != null) {

			final boolean existsAsAdminByMobile = adminsDao.existsByMobile(userDTO.getMobileNumber());
			if (existsAsAdminByMobile == true)
				throw new AcademicException("Mobile number already exists", Exceptions.BAD_REQUEST.name());

			final boolean existsAsStudentByMobile = studentDao.existsByMobile(userDTO.getMobileNumber());
			if (existsAsStudentByMobile == true)
				throw new AcademicException("Mobile number already exists", Exceptions.BAD_REQUEST.name());
		}

		if (!userDTO.getRoles().contains(Roles.Admin.name())) {

			user = mapper.map(userDTO, StudentDTO.class);
			appUser = new Student();

			appUser.setFirstName(user.getFirstName());
			appUser.setLastName(user.getLastName());
			appUser.setEmail(user.getEmail());
			appUser.setPassword(passwordEncoder.encode(user.getPassword()));
			((Student) appUser).addRoles(user.getRoles());
			if (user.getMobileNumber() != null) {
				appUser.setMobileNumber(user.getMobileNumber());
			}

			// save the new object to the database
			studentDao.save((Student) appUser);

		} else {

			user = mapper.map(userDTO, AdminsDTO.class);

			appUser = new Admins();

			appUser.setFirstName(user.getFirstName());
			appUser.setLastName(user.getLastName());
			appUser.setEmail(user.getEmail());
			appUser.setPassword(passwordEncoder.encode(user.getPassword()));
			((Admins) appUser).addRoles(user.getRoles());
			if (user.getMobileNumber() != null) {
				appUser.setMobileNumber(user.getMobileNumber());
			}

			// Save the new object to the database
			adminsDao.save((Admins) appUser);

		}

		return HttpStatus.CREATED.value();
	}

	// authentication method implemented manually due to the need to authenticate
	// users mapped to different database tables, who might either be
	// Admin or student. Still wish I can delegate this to the
	// AuthenticationMangager bean to authenticate automatically
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AppUserDTO> T signIn(AuthenticationRequest request, String role) {

		String username = request.getEmail();
		String password = request.getPassword();

		// check if the user is an Admin
		if (role.equalsIgnoreCase("admin")) {
			Optional<Admins> optionalAdmin = adminsDao.findByEmail(username);
			if (optionalAdmin.isPresent() && passwordEncoder.matches(password, optionalAdmin.get().getPassword())) {

				Admins admin = optionalAdmin.get();
				var accessToken = jwtUtil.generateToken(admin);
				var refreshToken = jwtUtil.createRefreshToken(admin);
				var dto = mapper.map(admin, AdminsDTO.class);
				dto.setAccessToken(accessToken);
				dto.setRefreshToken(refreshToken);

				return (T) dto;
			}

		} else {

			// Then the user might be a student
			Optional<Student> optionalStudent = studentDao.findByEmail(username);
			if (optionalStudent.isPresent() && passwordEncoder.matches(password, optionalStudent.get().getPassword())) {

				Student student = optionalStudent.get();
				var accessToken = jwtUtil.generateToken(student);
				var dto = mapper.map(student, StudentDTO.class);
				dto.setAccessToken(accessToken);
				return (T) dto;
			}

		}

		// the user does not exist in the database
		throw new AcademicException(role.equalsIgnoreCase("admin") ? "admin not found!" : "student not found!",
				Exceptions.RECORD_NOT_FOUND.name());

	}

	
	
//	Implementation that generates new token using the refresh token (for user validation) after the expiration of the existing token
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AppUserDTO> T generateNewToken(String refreshToken, HttpServletResponse response) throws IOException {
		
		
//		extract username from the token
		var email = jwtUtil.extractUsername(refreshToken);
		var roles = jwtUtil.extractRoles(refreshToken);
		
		String role = roles.get(0);
		switch (role.toLowerCase()) {
		case "admin": {
			
			Optional<Admins> optional = adminsDao.findByEmail(email);
			if(optional.isEmpty()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized!");
				return null;
			}
			
			Admins admin = optional.get();
			String token = jwtUtil.generateToken(admin);
			;			
			var dto = mapper.map(admin, AdminsDTO.class);
			
			dto.setAccessToken(token);
			
			return (T)dto;
			
		}
		
		case "student":{
			
			Optional<Student> optional = studentDao.findByEmail(email);
			
			if(optional.isEmpty()) {
				
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized!");
			return null;
			}
			
			Student student = optional.get();
			String token = jwtUtil.generateToken(student);
			var dto = mapper.map(student, StudentDTO.class);
			
			dto.setAccessToken(token);
			
			return (T)dto;
			
		}
		}
		

		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized!");
		
		return null;
		
	}

}
