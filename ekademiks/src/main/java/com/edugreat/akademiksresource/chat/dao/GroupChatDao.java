package com.edugreat.akademiksresource.chat.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;

import com.edugreat.akademiksresource.chat.model.GroupChat;
import com.edugreat.akademiksresource.chat.projection.GroupChatInfo;

@RestResource(exported = false)
public interface GroupChatDao extends JpaRepository<GroupChat, Integer> {

	@Query("SELECT g FROM GroupChat g")
	List<GroupChatInfo> getGroupInfo(Integer studentId);
	
//	get the admin ID for the group chat referenced by the groupChatId
	@Query("SELECT g.groupAdminId FROM GroupChat g WHERE g.id =:groupChatId")
	Integer findAdminId(Integer groupChatId);

	@Query("SELECT g.groupAdminId FROM GroupChat g WHERE g.id =:groupId")
	Integer getAdminId(Integer groupId);
	
	
}
