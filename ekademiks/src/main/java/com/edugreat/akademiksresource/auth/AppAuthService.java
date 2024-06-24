package com.edugreat.akademiksresource.auth;

import java.util.Optional;

import javax.transaction.Transactional;

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
public class AppAuthService implements AppAuthInterface{
	
	private final AdminsDao adminsDao;
	private final StudentDao studentDao;
	private final JwtUtil jwtUtil;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper mapper;
	
	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public int  signUp(AppUserDTO userDTO) {
		AppUserDTO user = null;
		AppUser appUser = null;
		//Check the type of user wanting to sign up.
		//if the intending user is a student
		
		
		//check if the user already exists in the student table
		final boolean existsAsStudentByEmail = studentDao.existsByEmail((userDTO.getEmail()));
		
		if(existsAsStudentByEmail) {
			throw new AcademicException("Email already exists", Exceptions.BAD_REQUEST.name());
		}
		
		//checks if the user already exists in the admin table
	    final boolean existsAsAdminByEmail = adminsDao.existsByEmail((userDTO.getEmail()));
	    if(existsAsAdminByEmail) {
			throw new AcademicException("Email already exists", Exceptions.BAD_REQUEST.name());
		}	
		
		//check if mobile number exists
		if(userDTO.getMobileNumber() != null) {
			
			final boolean existsAsAdminByMobile = adminsDao.existsByMobile(userDTO.getMobileNumber());
			if(existsAsAdminByMobile == true) throw new AcademicException("Mobile number already exists", Exceptions.BAD_REQUEST.name());
		
			final boolean existsAsStudentByMobile = studentDao.existsByMobile(userDTO.getMobileNumber());
			if(existsAsStudentByMobile == true) throw new AcademicException("Mobile number already exists", Exceptions.BAD_REQUEST.name());
		}
		
		
		
		if(! userDTO.getRoles().contains(Roles.Admin.name())) {
			
			
			user = mapper.map(userDTO, StudentDTO.class);
			appUser  = new Student();
			
			appUser.setFirstName(user.getFirstName());
			appUser.setLastName(user.getLastName());
			appUser.setEmail(user.getEmail());
			appUser.setPassword(passwordEncoder.encode(user.getPassword()));
			((Student) appUser).addRoles(user.getRoles());
			if(user.getMobileNumber() != null) {
				appUser.setMobileNumber(user.getMobileNumber());
			}
			
			//save the new object to the database 
			studentDao.save((Student)appUser);
			
			
		}else {
			
			
			
			user = mapper.map(userDTO, AdminsDTO.class);
			
			appUser  = new Admins();
			
			appUser.setFirstName(user.getFirstName());
			appUser.setLastName(user.getLastName());
			appUser.setEmail(user.getEmail());
			appUser.setPassword(passwordEncoder.encode(user.getPassword()));
			((Admins) appUser).addRoles(user.getRoles());
			if(user.getMobileNumber() != null) {
				appUser.setMobileNumber(user.getMobileNumber());
			}
			
			//Save the new object to the database 
			adminsDao.save((Admins)appUser);
			
			
			
		}
		
		
		
		return HttpStatus.CREATED.value();
	}
	
	
	//authentication method implemented manually due to the need to authenticate users mapped to different database tables, who might either be
	//Admin or student. Still wish I can delegate this to the AuthenticationMangager bean to authenticate automatically 
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AppUserDTO> T signIn(AuthenticationRequest request) {
	
		String username = request.getEmail();
		String password = request.getPassword();
		//check if the user is an Admin
		Optional<Admins> optionalAdmin = adminsDao.findByEmail(username);
		if(optionalAdmin.isPresent() && passwordEncoder.matches(password, optionalAdmin.get().getPassword())) {
			
			Admins admin = optionalAdmin.get();
			var jwt = jwtUtil.generateToken(admin);
			var dto = mapper.map(admin, AdminsDTO.class);
			dto.setToken(jwt);
			
			return (T)dto;
		}
		
		//Then the user might be a student
		Optional<Student> optionalStudent = studentDao.findByEmail(username);
		if(optionalStudent.isPresent() && passwordEncoder.matches(password, optionalStudent.get().getPassword())) {
			
			Student student = optionalStudent.get();
			var jwt = jwtUtil.generateToken(student);
			var dto = mapper.map(student, StudentDTO.class);
			dto.setToken(jwt);
			return (T)dto;
		}
		
		//the user does not exist in the database
		throw new AcademicException("user not found!", Exceptions.RECORD_NOT_FOUND.name());
		
	}



}
