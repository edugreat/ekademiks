package com.edugreat.akademiksresource.auth;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AuthenticationResponse implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private String toke;

}
