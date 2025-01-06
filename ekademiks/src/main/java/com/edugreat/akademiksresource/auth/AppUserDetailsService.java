package com.edugreat.akademiksresource.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.dao.AdminsDao;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.model.AppUser;

@Service

public class AppUserDetailsService implements UserDetailsService {

	@Autowired
	private AdminsDao adminsDao;

	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private JwtUtil jwtUtil;
	

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		return loadByUsername(email);
	}

	private AppUser loadByUsername(String username) {

		final boolean isStudent = studentDao.existsByEmail(username);
		if (isStudent)
			return studentDao.findByEmail(username).orElseThrow(
					() -> new AcademicException("Error authenticating user", Exceptions.STUDENT_NOT_FOUND.name()));

		return adminsDao.findByEmail(username)
				.orElseThrow(() -> new AcademicException("Error authenticating admin", Exceptions.BAD_REQUEST.name()));

	}

//	this method is utilized at the endpoint that retrieves chat messages. It is used to test if the receipient is a valid group logged user
	public boolean isValidRequest(String token) {
		
		
		final String username = jwtUtil.extractUsername(token);
		
		if(username == null || SecurityContextHolder.getContext().getAuthentication() == null) return false;
		
		
		final UserDetails userDetails = loadUserByUsername(username);
		
		return jwtUtil.isTokenValid(token, userDetails);
	}
}
