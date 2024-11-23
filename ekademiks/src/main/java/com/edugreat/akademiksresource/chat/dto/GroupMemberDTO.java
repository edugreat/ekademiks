package com.edugreat.akademiksresource.chat.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GroupMemberDTO {
	
	@Min(value = 0, message = "id not found")
	private Integer id;
	
	private LocalDateTime joinedAt;
	
	@Min(value = 0, message = "group id not found")
	private Integer groupId;
	
	@Min(value = 0, message = "member id not found")
	private Integer memberId;

}
