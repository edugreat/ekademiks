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
import lombok.Data;
import lombok.NoArgsConstructor;

// Entity represents a single message or conversation in a particular chat group at a particular time
@Entity
@Table
@Data
@NoArgsConstructor
public class Chat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	//@Setter(AccessLevel.NONE)
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
	//@Setter(AccessLevel.NONE)
	private LocalDateTime sentAt = LocalDateTime.now();
	
//	Hold reference to the chat this chat replied to, if it was a replied chat
	@Column(nullable = true, name = "replied_to")
	private Integer repliedTo;
	
//	the content of chat that this chat replied to. It's set to null once the original chat has been deleted 
	@Column(nullable = true)
	private String repliedToChat;
	
	
	public Chat(GroupChat groupChat, Student sender, String content) {
		
		
		this.groupChat = groupChat;
		this.sender = sender;
		this.content = content;
		
	}
	
	

}
