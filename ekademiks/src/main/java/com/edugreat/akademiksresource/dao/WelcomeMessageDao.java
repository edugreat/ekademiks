package com.edugreat.akademiksresource.dao;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edugreat.akademiksresource.model.WelcomeMessage;

public interface WelcomeMessageDao extends JpaRepository<WelcomeMessage, Integer> {

	@Query("SELECT m FROM WelcomeMessage w JOIN w.messages m")
    Collection<String> findAllMessages();
}
