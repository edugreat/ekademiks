package com.edugreat.akademiksresource.chat.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupChatDTO {
	
	@Min(value = 0, message = "invalid id")
	private Integer id;
	
	@Min(value = 0, message = "invalid admin id")
	private Integer groupAdminId;
	
	@NotNull(message = "No group description")
	@NotEmpty(message = "No group description")
	private String description;
	
	@NotNull(message ="No group icon url")
	@NotEmpty(message = "No group icon url")
	private String groupIconUrl;
	
	@NotEmpty(message = "No group name found")
	private String groupName;
	
	private LocalDateTime createdAt;

	public GroupChatDTO(
			@NotNull(message = "No group description") @NotEmpty(message = "No group description") String description,
			@NotNull(message = "No group icon url") @NotEmpty(message = "No group icon url") String groupIconUrl,
			@NotEmpty(message = "No group name found") String groupName,
			@Min(value = 0, message = "invalid admin id") Integer groupAdmin) {
		
		
		this.description = description;
		this.groupIconUrl = groupIconUrl;
		this.groupName = groupName;
		this.groupAdminId = groupAdmin;
	}
	
	

}
