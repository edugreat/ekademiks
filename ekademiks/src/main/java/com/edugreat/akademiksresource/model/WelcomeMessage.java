package com.edugreat.akademiksresource.model;

import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
