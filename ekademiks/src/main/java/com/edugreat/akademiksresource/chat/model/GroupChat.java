package com.edugreat.akademiksresource.chat.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Represents chat group for users of the application, especially students

@Entity
@Table(name = "group_chat")
@Data
@NoArgsConstructor
public class GroupChat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
//	represents chat group name
	@Column(nullable = false)
	private String groupName;
	
//	when the group was created
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
//	tells who created the group. The id of the student that created group
	@Column(nullable = false)
	private Integer groupAdminId;
	
//	The write-up describing the group
	@Column(nullable = false)
	private String description;
	
//	Represents link for an icon used in a group chat. The icons would be stored in the front-end
	@Column(nullable = false)
	private String groupIconUrl;
	
//	only admins can post to locked groups
	@Column
	private Boolean isGroupLocked = false;
	
	@OneToMany(mappedBy = "groupChat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Setter(AccessLevel.NONE)//only GroupMembers should handle the bi-diractional relationship
	private List<GroupMember> groupMembers = new ArrayList<>();
	
	@OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<Chat> chats = new ArrayList<>();
	
	
	
	public GroupChat(String groupName, Integer groupAdminId, String description,String groupIconUrl) {
		
		this.groupName = groupName;
		this.groupAdminId = groupAdminId;
		this.description = description;
		this.groupIconUrl = groupIconUrl;
		
		
	}
	
	@PrePersist
	protected void onCreated() {
		
		this.createdAt = LocalDateTime.now();
	}
	
	public void AddMessage(Chat chat) {
		
		if(!chats.contains(chat)) {

			chats.add(chat);
			chat.setGroupChat(this);
		}
		
		
	}


}
