package com.edugreat.akademiksresource.contract;

import java.io.IOException;

import com.edugreat.akademiksresource.auth.AuthenticationRequest;
import com.edugreat.akademiksresource.controller.StudentRegistrationData;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.instructor.InstructorRegistrationRequest;
import com.edugreat.akademiksresource.model.AppUser;

import jakarta.servlet.http.HttpServletResponse;

/*
 * Interface which defines contracts for the users of the app
 */
public interface AppAuthInterface {

	// Contract that registers new user
	public int studentSignup(StudentRegistrationData registrationData);

	// Allows users to sign in using different role preferences
	public <T extends AppUserDTO> T signIn(AuthenticationRequest request, String role);
	
//	Generates new token using the refresh token to validate the user
	public<T extends AppUserDTO> T generateNewToken (String refreshToken, HttpServletResponse response) throws IOException;

//	return redis cached logged in user
	<T extends AppUserDTO> T getCachedUser(String cachingKey);
	
//	this method is used to reset the content of the user cache, especially when they've left a group chat.
//	In this scenario, we check if they still belong to a group, setting appropriate parameters
	<T extends AppUserDTO> void resetCachedUser(T user, String cachingKey);
	
	public void instructorSignup(InstructorRegistrationRequest request);
	}
	

	


