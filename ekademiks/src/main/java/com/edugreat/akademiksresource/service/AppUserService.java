package com.edugreat.akademiksresource.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.contract.AppUserInterface;
import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.Admins;
import com.edugreat.akademiksresource.model.Student;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class AppUserService implements AppUserInterface {
	
	private final StudentDao studentDao;
	private final AdminsDao adminsDao;
	private final ModelMapper mapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void updatePassword(AppUserDTO dto) {
		
		//check if the user exists in the database
		final boolean isStudent = studentDao.existsByEmail(dto.getEmail());
		if(isStudent) {
			Student student = studentDao.findByEmail(dto.getEmail()).get();
			student.setPassword(passwordEncoder.encode(dto.getPassword()));
			return;
			
		}else {
			final boolean isAdmins = adminsDao.existsByEmail(dto.getEmail());
			if(isAdmins) {
				
				Admins admins = adminsDao.findByEmail(dto.getEmail()).get();
				admins.setPassword(passwordEncoder.encode(dto.getPassword()));
				return;
			}
		}
		
		throw new AcademicException("User not found", Exceptions.RECORD_NOT_FOUND.name());
		
	}

	@Override
	public  AppUserDTO  searchByEmail(String email) {
		
		return searchUser(email);
	}

	@Override
	public List<StudentDTO> allStudents() {
		
		return studentDao.findAll().stream().map(student -> this.mapToStudentDTO(student)).collect(Collectors.toList());
	}

	@Override
	public List<AdminsDTO> allAdmins() {
		
		return adminsDao.findAll().stream().map(admins -> this.mapToAdminsDTO(admins)).collect(Collectors.toList());
	}
	
	@Override
	@Transactional
	public void deleteUser(String email) {
		final boolean isStudent = studentDao.existsByEmail(email);
		if(isStudent) {
			studentDao.deleByEmail(email);
			return;
		}else if(!isStudent) {
			final boolean isAdmin = adminsDao.existsByEmail(email);
			if(isAdmin)
				adminsDao.deleteByEmail(email);
		}
		
		
	}

	private AppUserDTO searchUser(String username) {
		
		final boolean isStudent = studentDao.existsByEmail(username);
		if(isStudent)
			return this.mapToStudentDTO(studentDao.findByEmail(username).get());
		else if(! isStudent) return this.mapToAdminsDTO(adminsDao.findByEmail(username).get());
		
		else throw new AcademicException("user not found", Exceptions.RECORD_NOT_FOUND.name());
	}
	
	private StudentDTO mapToStudentDTO(Student student) {
		
		return mapper.map(student, StudentDTO.class);
		
	}
	
	private AdminsDTO mapToAdminsDTO(Admins admins) {
		
		return mapper.map(admins, AdminsDTO.class);
	}

}
