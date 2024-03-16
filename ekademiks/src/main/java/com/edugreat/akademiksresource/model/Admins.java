package com.edugreat.akademiksresource.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.edugreat.akademiksresource.enums.Roles;

@Entity
@Table
public class Admins extends AppUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Admins() {
		super();
		
	}

	public Admins(String firstName, String lastName, String email, String mobileNumber, String password) {
		super(firstName, lastName, email, mobileNumber, password);
		
	}

	public Admins(String firstName, String lastName, String email, String password) {
		super(firstName, lastName, email, password);
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		Set<GrantedAuthority> authorities = new HashSet<>();
		for(Roles role: super.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.name()));
			
		}
		
		return authorities;
	}
	
	
	
	
	

}
