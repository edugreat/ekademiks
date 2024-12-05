package com.edugreat.akademiksresource.chat.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.edugreat.akademiksresource.model.Student;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Entity class that keeps records of a group and its members

@Entity
@Table
@Data
@NoArgsConstructor
public class GroupMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer id;
	
	@ElementCollection
	@CollectionTable(name = "member_roles",
	joinColumns = @JoinColumn(name = "group_members_id")
			)
	@Column(name = "roles")
	private Set<String> roles = new HashSet<>();
	
	@ManyToOne()
	@JoinColumn(name = "group_id", nullable = false)
	private GroupChat groupChat;
	
	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Student member;
	
//	When a user joins the group
	@Column(nullable = false)
	private LocalDateTime joinedAt;
	
	public GroupMember(GroupChat groupChat, Student member) {
		
		this.groupChat = groupChat;
		groupChat.getGroupMembers().add(this); //for bi-directional relationship
		
		this.member = member;
		member.getGroupMembers().add(this); //for bi-directional relationship
		
	}
	
	@PrePersist
	protected void prePersist() {
		
		this.joinedAt = LocalDateTime.now();
		addRole("member"); // new members have the 'member' default role
		
	}
	
	public void addRole(String role) {
		
		roles.add(role);
	}
	
	
	
}
