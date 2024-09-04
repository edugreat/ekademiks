package com.edugreat.akademiksresource.contract;

import com.edugreat.akademiksresource.auth.AuthenticationRequest;
import com.edugreat.akademiksresource.dto.AppUserDTO;

/*
 * Interface which defines contracts for the users of the app
 */
public interface AppAuthInterface {

	// Contract that registers new user
	public int signUp(AppUserDTO dto);

	// Allows users to sign in using different role preferences
	public <T extends AppUserDTO> T signIn(AuthenticationRequest request, String role);

}
