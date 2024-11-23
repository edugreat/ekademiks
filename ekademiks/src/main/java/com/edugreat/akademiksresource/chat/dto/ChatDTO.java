package com.edugreat.akademiksresource.chat.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatDTO {
	
	@Min(value = 0)
	private Integer id;
	@Min(value = 0)
	private Integer groupId;
	
	@Min(value = 0)
	private Integer senderId;
	
	@Transient
	private String senderName;
	
//	keeps tracks of those who are currently online when this chat instance was created.
//	This is used to show and update the number of group members participating in the chat.
	@Transient
	private int onlineMembers;
	
	@NotNull(message = "chat message is missing")
	@NotEmpty(message = "chat message is missing")
	private String content;
	
	private LocalDateTime sentAt;

	public ChatDTO(@Min(0) Integer id, @Min(0) Integer groupId, @Min(0) Integer senderId,
			@NotNull(message = "chat message is missing") @NotEmpty(message = "chat message is missing") String content,
			LocalDateTime sentAt) {
	
		this.id = id;
		this.groupId = groupId;
		this.senderId = senderId;
		this.content = content;
		this.sentAt = sentAt;
	}
	
	
}
