package com.edugreat.akademiksresource.chat.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.chat.model.Chat;

@RestResource(exported = false)
@Repository
public interface ChatDao extends JpaRepository<Chat, Integer> {

//	get all the chats in the group, ordering them by the dates they were sent
	//@Query("SELECT c FROM Chat c WHERE c.groupChat.id =:groupId ORDER BY c.sentAt asc")
	List<Chat> findByGroupChatIdOrderBySentAt(Integer groupId);
	
//	checks if the group chat whose ID is referenced has had any previous post(s)
	@Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Chat c WHERE c.groupChat.id =:groupId")
	boolean hasPreviousPosts(Integer groupId);
	
	@Query("SELECT c FROM Chat c WHERE c.groupChat.id =:groupId")
	List<Chat> findByGroupId(Integer groupId);
	
//	get a list of replied chats to the given chat (chatId) belonging to the given groupId
	@Query("SELECT c FROM Chat c WHERE c.groupChat.id =:groupId  AND c.repliedTo =:chatId")
	List<Chat> findRepliedChats(Integer chatId, Integer groupId);

}
