package com.edugreat.akademiksresource.contract;

import com.edugreat.akademiksresource.dto.AppUserDTO;

/*
 * Interface which defines contracts for the users of the app
 */
public interface AppAuthInterface {

	//Contract that registers new user
	public <T extends AppUserDTO> T signUp(T dto) ;
	

	public <T extends AppUserDTO> T signIn(T dto);
	
}
