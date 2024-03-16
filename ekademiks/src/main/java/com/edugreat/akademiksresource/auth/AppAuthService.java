package com.edugreat.akademiksresource.auth;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
	private final AuthenticationManager authenticationManager;
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
				throw new AcademicException("Admin already exists", Exceptions.BAD_REQUEST.name());
			}
			user = mapper.map(userDTO, AdminsDTO.class);
			appUser  = new Admins();
			appUser.setFirstName(user.getFirstName());
			appUser.setLastName(user.getLastName());
			appUser.setEmail(user.getEmail());
			appUser.setPassword(passwordEncoder.encode(user.getPassword()));
			appUser.setRoles(user.getUserRoles());
			if(user.getMobileNumber() != null) {
				appUser.setMobileNumber(user.getMobileNumber());
			}
			
			//Save the new object to the database and return it
			appUser = adminsDao.save((Admins)appUser);
			
			//map to it's dto for display
			user = mapper.map(appUser, AdminsDTO.class);
			
		}
		
		
		return (T)user;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends AppUserDTO> T signIn(T dto) {
		
		AppUser appUser = new AppUser();
		AppUserDTO userDTO = null;
		var roles = dto.getRoles();
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
			//if the user that wants to sign in is an admin, then retrieve the object from the admin dao,else it's a student
			var user = (roles.contains(Roles.Admin.name())) ? 
					adminsDao.findByEmail(dto.getEmail()).orElseThrow(()-> new AcademicException("Admin user not found", Exceptions.RECORD_NOT_FOUND.name())) :
						studentDao.findByEmail(dto.getEmail()).orElseThrow(() -> new AcademicException("Student not found", Exceptions.STUDENT_NOT_FOUND.name()));
			
			var jwt = jwtUtil.generateToken(user);
			
			userDTO = mapper.map(user, AppUserDTO.class);
			userDTO.setToken(jwt);
			userDTO.setStatusCode(200);
			
			
			
		
			
		} catch (Exception e) {
			userDTO.setStatusCode(500);
			userDTO.setSignInErrorMessage(e.getMessage());
			
			return (T) userDTO;
		}
		
		
		return (T) mapper.map(appUser, AppUserDTO.class);
		
	}



}
