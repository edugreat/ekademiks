package com.edugreat.akademiksresource.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

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

	@ElementCollection(fetch = FetchType.EAGER)
	private Set<UserRoles> roles = new HashSet<>();

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
		for (String role : this.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role));

		}

		return authorities;
	}

	public Set<String> getRoles() {
		return roles.stream().map(role -> role.getRole().toString()).collect(Collectors.toSet());
	}

	public void setRoles(Set<UserRoles> roles) {
		this.roles = roles;
	}

	public void addRoles(Set<String> userRoles) {

		for (String role : userRoles) {
			this.roles.add(new UserRoles(Roles.valueOf(role)));
		}

	}

}
