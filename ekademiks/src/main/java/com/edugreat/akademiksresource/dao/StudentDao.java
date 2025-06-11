package com.edugreat.akademiksresource.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.AssignmentResponse;
import com.edugreat.akademiksresource.model.MiscellaneousNotifications;
import com.edugreat.akademiksresource.model.Student;

@Repository
@RepositoryRestResource(exported = false)
public interface StudentDao extends JpaRepository<Student, Integer> {

	// checks the the existence of a student by their email
	@Query("SELECT CASE WHEN COUNT(s.email) > 0 THEN true ELSE false END FROM Student s WHERE s.email =:email")
	 boolean existsByEmail(String email);

	// checks the existence of a student by their phone number
	@Query("SELECT CASE WHEN COUNT(s.mobileNumber) > 0 THEN true ELSE false END FROM Student s WHERE s.mobileNumber =:mobile")
	 boolean existsByMobile(String mobile);

	// finds student by their phone number
	 Student findByMobileNumber(String mobileNumber);

	// finds student by their email address
	 Optional<Student> findByEmail(String email);

	@Modifying
	@Query("DELETE FROM Student s WHERE s.email =:email")
	 void deleByEmail(@Param("email") String email);

//	select all students that were notified
	@Query("SELECT s FROM Student s JOIN s.assessmentNotifications n ON n.id =:notificationId")
	List<Student> assessmentNotifiedStudents(Integer notificationId);

//	Counts the number of students that have not read a given notification
//	This count is used to decide if the notification should be deleted from the database or kept
	@Query("SELECT COUNT(n) FROM Student s JOIN s.assessmentNotifications n ON n.id =:notificationId")
	int getUnreadNotificationCountForStudents(Integer notificationId);
	
	
//	get all unread messages for the student referenced by the student id
	@Query("SELECT s.unreadChats FROM Student s WHERE s.id =:studentId")
	 SortedMap<Integer, Integer> unreadChats(Integer studentId);
	
	
//	get notifications by type belonging to the student referenced by the given ID
	@Query("SELECT n FROM Student s JOIN s.miscellaneousNotices n WHERE s.id =:studentId AND n.type =:type")
	Set<MiscellaneousNotifications> findNotificationsByType(Integer studentId, String type);

//	returns the first name of the student referenced by the given student ID
	@Query("SELECT s.firstName FROM Student s WHERE s.id =:studentId")
	String getFirstName(Integer studentId);
	
//	select all notifications of the given types belonging to the given student referenced by studentId.
//	Basically, this query method selects notifications about request to join group chat as well as those about request approval
	@Query("SELECT n FROM Student s JOIN s.miscellaneousNotices n WHERE s.id =:studentId AND n.type LIKE CONCAT('%',:joinRequest,'%') OR n.type LIKE CONCAT('%',:reqApproved,'%')")
	Set<MiscellaneousNotifications> findNotificationByType(Integer studentId, String joinRequest, String reqApproved);

//	get IDs of the group chats the user has requested to join
	@Query("SELECT s.pendingGroupChatRequests FROM Student s WHERE s.id =:studentId")
	Set<Integer> pendingGroupRequestsFor(Integer studentId);

//	get the number of students yet to read the given miscellaneous notification
	@Query("SELECT COUNT(n) FROM Student s JOIN s.miscellaneousNotices n ON n.id =:notificationId")
	int getUnreadNotificationCount(Integer notificationId);

//	retrieves a student's institution id
	@Query("SELECT s.institution.id FROM Student s WHERE s.id =:studentId")
	Integer getMyInstitutionId(Integer studentId);

	@Query("SELECT s FROM Student s JOIN s.assignmentResponses ar ON ar.instructorId =:instructorId")
	List<Student> getAssessmentResponses(Integer instructorId);
	
	@Query("SELECT s.id FROM Student s WHERE s.email =:username")
	Integer getIdByUsername(String username);
	
	Integer findIdByFirstName(String firstName);
	

	
	
}
