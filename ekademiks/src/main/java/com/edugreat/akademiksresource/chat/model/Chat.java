package com.edugreat.akademiksresource.chat.model;

import java.time.LocalDateTime;

import com.edugreat.akademiksresource.model.Student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entity represents a single message or conversation in a particular chat group at a particular time
@Entity
@Table
@Data
@NoArgsConstructor
public class Chat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "group_id", nullable = false)
	private GroupChat groupChat;
	
	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private Student sender;
	
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = true)
	@Setter(AccessLevel.NONE)
	private LocalDateTime sentAt = LocalDateTime.now();
	
	public Chat(GroupChat groupChat, Student sender, String content) {
		
		
		this.groupChat = groupChat;
		this.sender = sender;
		this.content = content;
		
	}
	
	

}
