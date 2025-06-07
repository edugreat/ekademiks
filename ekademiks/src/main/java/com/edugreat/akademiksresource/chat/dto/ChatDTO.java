package com.edugreat.akademiksresource.chat.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatDTO {
	
	@Min(value = 0)
	private Integer id;
	@Min(value = 0)
	private Integer groupId;
	
	@Min(value = 0)
	private Integer senderId;
	
	private boolean editedChat = false;
	
	//@JsonIgnore
	@Transient
	private String senderName;
	
	//@JsonIgnore
	@Transient 
	Integer chatReceipient;
	
	
//	points to the chat ID this chat replied to, if it was a replied chat
	@Transient
	private Integer repliedTo;
	
//	shows the actual message that got the reply
	@Transient
	private String repliedToChat;
	
	
//	Points to the user who has deleted a chat that has some replies. This is used at the UI to show who deleted a particular Chat with replies.
	@Transient
	private Integer deleterId;
	
//	name of the user who deleted the chat
	@Transient
	private String deleter;

	
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
