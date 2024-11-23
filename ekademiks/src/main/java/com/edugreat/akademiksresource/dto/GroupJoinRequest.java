package com.edugreat.akademiksresource.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// An object the represents user's request to join a particular group chat
@Data
public class GroupJoinRequest {
	
	@NotNull
	private Integer groupId; //the group the user is requesting to join
	
	@NotNull
	private Integer requesterId; // the student requesting to join the group chat
	
	@NotNull
	private Integer groupAdminId; // the group admin to which the requester intends to join
	
	@NotNull
	private LocalDateTime requestedAt; // the time the request was made


	@NotNull
	private String requester; //the name of the user requesting to join the group chat
	
	
	
}
