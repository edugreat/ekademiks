package com.edugreat.akademiksresource.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.edugreat.akademiksresource.model.Admins;

//@RepositoryRestResource(collectionResourceRel = "admins",path = "admins", exported = false)
public interface AdminsDao extends JpaRepository<Admins, Integer> {

	// checks if an admin exists.Returns true if exists, otherwise, false
	@Query("SELECT CASE WHEN COUNT(a.email) > 0 THEN true ELSE false END FROM Admins a WHERE a.email =:email")
	public boolean existsByEmail(String email);

	// find an admin by their email
	public Optional<Admins> findByEmail(String email);

	@Query("DELETE FROM Admins WHERE email =:email")
	public void deleteByEmail(String email);

	@Query("SELECT CASE WHEN COUNT(a.mobileNumber) > 0 THEN true ELSE false END FROM Admins a WHERE a.mobileNumber =:mobile")
	public boolean existsByMobile(String mobile);

	

}
