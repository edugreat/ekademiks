package com.edugreat.akademiksresource.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.AppUser;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

	private final AdminsDao adminsDao;
	
	private final StudentDao studentDao;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		return loadByUsername(email);
	}
	
	private AppUser loadByUsername(String username) {
		
		final boolean isStudent = studentDao.existsByEmail(username);
		if(isStudent)
			return studentDao.findByEmail(username).orElseThrow(() -> new AcademicException("Error authenticating user", Exceptions.STUDENT_NOT_FOUND.name()));
		
		return adminsDao.findByEmail(username).orElseThrow(() -> new AcademicException("Error authenticating admin", Exceptions.BAD_REQUEST.name()));
		
		
	}
	

}
