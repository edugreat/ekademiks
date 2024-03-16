package com.edugreat.akademiksresource.contract;

import java.util.List;

import com.edugreat.akademiksresource.dto.AdminsDTO;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;

public interface AppUserInterface {


	//provides contract that updates student's password
	public  void updatePassword(AppUserDTO dto);
	
	
	//Searches a user by their email
	public AppUserDTO  searchByEmail(String email);
	
	
	public List<StudentDTO> allStudents();
	
	
	public List<AdminsDTO> allAdmins();
	
	public void deleteUser(String email);
	
}
