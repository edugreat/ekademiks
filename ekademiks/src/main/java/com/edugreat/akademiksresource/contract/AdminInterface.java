package com.edugreat.akademiksresource.contract;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;

/*
 * The contracts declared here are for user with the Admin roles.
 * User with roles not Admin are not expected to invoke the implementations of these contracts
 */
public interface AdminInterface {


	//provides contract that updates student's password
	public  void updatePassword(AppUserDTO dto);
	
	
	//Searches a user by their email
	public AppUserDTO  searchByEmail(String email);
	
	
	public List<StudentDTO> allStudents();
	
	
	public List<AdminsDTO> allAdmins();
	
	public void deleteUser(String email);
	
	public SubjectDTO setSubject(SubjectDTO dto);
	
	//sets new test
	public void setTest(TestDTO testDTO);
	
    public LevelDTO addLevel(LevelDTO dto);
	
	public Iterable<LevelDTO> findAllLevels();
	
	public void updateTest(Integer testId, Map<String, Object> updates);//method that updates existing Test object, intended to use the patch method
    
	public void createWelcomeMessages(Map<String, Collection<String>> msgs);
	
}
