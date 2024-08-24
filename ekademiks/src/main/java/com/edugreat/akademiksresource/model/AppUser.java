package com.edugreat.akademiksresource.model;

import java.util.Collection;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * This class models the user of the application in a broader context.
 *It is the base class for all users of the application
 */
@MappedSuperclass
@NoArgsConstructor
@Data
public class AppUser implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "mobile", unique = true, nullable = true)
	private String mobileNumber;

	@Column(name = "password", nullable = false)
	private String password;
	
	@Column
	private boolean accountEnabled = true;
	@Column
	private boolean lockedAccount = true;
	@Column
	private boolean expiredAccount = true;
	@Column
	private boolean expiredCredentials = true;
	

	public AppUser(String firstName, String lastName, String email, String mobileNumber, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.password = password;
	}

	public AppUser(String firstName, String lastName, String email, String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;

		this.password = password;
	}

	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return null;
	}

	@Override
	public String getUsername() {
		
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return isExpiredAccount();
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return isLockedAccount();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return isExpiredAccount();
	}

	@Override
	public boolean isEnabled() {
		
		return isAccountEnabled();
	}

	
	
}
