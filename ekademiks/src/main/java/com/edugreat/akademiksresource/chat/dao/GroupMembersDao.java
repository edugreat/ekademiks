package com.edugreat.akademiksresource.chat.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;

import com.edugreat.akademiksresource.chat.model.GroupMember;

@RestResource(exported = false)
public interface GroupMembersDao extends JpaRepository<GroupMember, Integer> {


//	checks if the student referenced by the studentId is a member of any group chat
	@Query("SELECT CASE WHEN COUNT(gm.member) > 0 THEN true ELSE false END FROM GroupMember gm WHERE gm.member.id =:studentId")
	boolean isGroupMember(Integer studentId);
	

//	get IDs of all the groups the student belongs to
	@Query("SELECT gm.groupChat.id FROM GroupMember gm WHERE gm.member.id =:studentId")
	List<Integer> groupIds(Integer studentId);
	
//	get the ids of all group members
	@Query("SELECT g.member.id FROM GroupMember g WHERE g.groupChat.id =:groupId")
	 List<Integer> getMemberIds(Integer groupId);


//	get the ids for all group chat the student referenced by the studentId belongs to
	@Query("SELECT gc.id FROM GroupMember gm JOIN gm.groupChat gc WHERE gm.member.id =:studentId")
	List<Integer> groupIdsFor(Integer studentId);

	

//	Returns true if the student referenced studentId belongs to the group chat referenced by groupId
	@Query("SELECT CASE WHEN COUNT(gm.member) > 0 THEN true ELSE false END FROM GroupMember gm JOIN gm.groupChat gc WHERE gc.id =:groupId AND gm.member.id =:studentId")
	boolean isGroupMember(Integer groupId, Integer studentId);


//	get when the given member joined the given group chat
	@Query("SELECT gm.joinedAt FROM GroupMember gm JOIN gm.member m WHERE m.id =:memberId AND gm.groupChat.id =:groupId")
	LocalDateTime findJoinedDate(Integer memberId, Integer groupId);
}
