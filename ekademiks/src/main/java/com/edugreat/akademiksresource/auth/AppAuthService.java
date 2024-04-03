package com.edugreat.akademiksresource.auth;

import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;

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
	public <T extends AppUserDTO> T signUp(T userDTO) {
		AppUserDTO user = null;
		AppUser appUser = null;
		//Check the type of user wanting to sign up.
		//if the intending user is a student
		if(! userDTO.getRoles().contains(Roles.Admin.name())) {
			
			
			//check if the user already exists
			final boolean exists = studentDao.existsByEmail((userDTO.getEmail()));
			if(exists) {
				throw new AcademicException("Student already exists", Exceptions.BAD_REQUEST.name());
			}
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
			
			//save the new object to the database and return it
			appUser = studentDao.save((Student)appUser);
			
			//map to the dto object for display 
			user = mapper.map(appUser, StudentDTO.class);
			
		}else {
			
			
			final boolean alreadyExists = adminsDao.existsByEmail(userDTO.getEmail());
			if(alreadyExists) {
				System.out.println(userDTO.getEmail()+" exists");
				throw new AcademicException("Admin already exists", Exceptions.BAD_REQUEST.name());
			}
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
			
			//Save the new object to the database and return it
			appUser = adminsDao.save((Admins)appUser);
			
			//map to it's dto for display
			user = mapper.map(appUser, AdminsDTO.class);
			
		}
		
		
		
		return (T) user;
	}
	
	
	//authentication method implemented manually due the the need to authenticate users mapped to different database table, who might either be
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
