package com.edugreat.akademiksresource.dao;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.WelcomeMessage;

@Repository
@RepositoryRestResource(exported = false)
public interface WelcomeMessageDao extends JpaRepository<WelcomeMessage, Integer> {

	@Query("SELECT m FROM WelcomeMessage w JOIN w.messages m")
	Collection<String> findAllMessages();
}
