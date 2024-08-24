package com.edugreat.akademiksresource.model;

import java.util.ArrayList;
import java.util.Collection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class WelcomeMessage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	
	@ElementCollection
	@CollectionTable(name = "welcome_msg")
	@Column(name = "welcome_msg", length = 2000)
	private Collection<String> messages = new ArrayList<>();
	
	
	


	public Collection<String> getMessages() {
		return messages;
	}



	public Integer getId() {
		return id;
	}
	
	public void addMessage(String message) {
		
		messages.add(message);
	}
	
	
	

}
