package com.edugreat.akademiksresource.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.model.Student;

import lombok.AllArgsConstructor;
@AllArgsConstructor
public class UserPrincipal implements UserDetails{

	
	private static final long serialVersionUID = 1L;
	
	private final Student student;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO To be re-implemented after some roles have been assigned to users of the application
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		
		return student.getEmail();	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

}
