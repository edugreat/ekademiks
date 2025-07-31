package com.edugreat.akademiksresource.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import com.edugreat.akademiksresource.model.MiscellaneousNotifications;
import com.edugreat.akademiksresource.model.Student;

@Repository

@RepositoryRestResource(path = "students")
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

	@RestResource(path = "A")
	Page<Student> findDistinctByStatus(@Param("status") String status, Pageable pageable);

	@RestResource(path = "B")
   // @Query("SELECT DISTINCT s FROM Student s JOIN s.institution i WHERE s.status LIKE CONCAT('%', :status, '%') AND i.id =:in")
	Page<Student> findDistinctByStatusAndInstitutionId(@Param("s") String s, @Param("in") Integer in,
			Pageable pageable);

//	QUERY ALL STUDENTS IN A PARTICULAR INSTITUTION(Used by institution admin and super admins to query all their students)
	@RestResource(path = "C")
	@Query("SELECT DISTINCT s FROM Student s JOIN s.institution i WHERE i.id =:id")
	Page<Student> findDistinctByInstitutionId(@Param("id") Integer id, Pageable pageable);

	
	@RestResource(path = "D")
	@Query("SELECT s FROM Student s JOIN s.instructors i JOIN s.institution ins WHERE i.id =:instr AND ins.id =:inst "
			+ "Order By s.firstName ASC")
	Page<Student> findByInstructorAndInstitution(@Param("instr") Integer instr, @Param("inst") Integer inst,
			Pageable pageable);

	
	@RestResource(path = "E")
	@Query("SELECT s FROM Student s JOIN s.instructors i JOIN s.institution ins WHERE s.status =:status AND"
			+ " i.id =:instr " + "Order By s.firstName ASC")
	Page<Student> findInstructorsStudentsByStatus(@Param("status") String status, @Param("instr") Integer instr,
			Pageable pageable);

	@RestResource(path = "F")
	@Query("SELECT s FROM Student s JOIN s.instructors i JOIN s.institution ins WHERE s.status =:status AND"
			+ " i.id =:instr AND ins.id =:inst " + "Order By s.firstName ASC")
	Page<Student> findInstructorsStudentsByInstitutionAndStatus(@Param("status") String status,
			@Param("inst") Integer inst, @Param("instr") Integer instr, Pageable pageable);


	@RestResource(path = "G")
	Page<Student> findDistinctByInstitutionCreatedBy(@Param("id") Integer id, Pageable pageable);

	
	@RestResource(path = "H")
	@Query("SELECT DISTINCT s FROM Student s JOIN s.institution inst WHERE s.status =:status AND "
			+ "inst.createdBy =:by AND inst.id =:in")
	Page<Student> findDistinctByInstitutionIdAndStatus(@Param("in") Integer in, 
			                                           @Param("status") String status,
			                                           @Param("by") Integer by,
			                                           Pageable pageable);

	@RestResource(path = "I")
	@Query("SELECT s FROM Student s JOIN s.institution inst WHERE s.status =:status AND inst.createdBy =:by")
	Page<Student> findByStatusAndCreatedBy(@Param("status")String status, 
			                             @Param("by")String by,
			                             Pageable pageable);
	@RestResource(path = "J")
	@Query("SELECT s FROM Student s JOIN s.instructors i WHERE i.id =:instr")
	Page<Student> findDistinctByInstructorsId(@Param("instr")Integer instr, Pageable pageable);
	
	@RestResource(exported = false)
	List<Student> findByClassroomId(Integer classroomId);
	
	

	

//	Methods that fetch student for classroom enrollment(for instructors only)
	@RestResource(path = "all")
	@Query("SELECT DISTINCT s FROM Student s JOIN s.institution i JOIN i.instructors instr WHERE instr.id =:instr")
	Page<Student> findAllByInstructorInTheirInstitution(@Param("instr")Integer instr, Pageable pageble);
	
	@RestResource(path = "xx")
	@Query("SELECT s FROM Student s JOIN s.institution i JOIN i.instructors instr WHERE instr.id =:instr AND i.id =:inst")
	Page<Student> findAllByInstitution(@Param("instr")Integer instr, @Param("inst")Integer inst, Pageable pageable);
	
	@RestResource(path ="xy")
	@Query("SELECT s FROM Student s JOIN s.institution i JOIN i.instructors instr WHERE instr.id =:instr "
			+ "AND s.status LIKE CONCAT('%',:status,'%')")
	Page<Student> findAllByStatus(@Param("instr")Integer instr, @Param("status")String status, Pageable pageable);
	
	@RestResource(path = "xyz")
	@Query("SELECT DISTINCT s FROM Student s JOIN s.institution i JOIN i.instructors instr WHERE s.status LIKE CONCAT('%',:status,'%')"
			+ " AND instr.id =:instr AND i.id =:inst")
	Page<Student> findAllByInstitutionAndStatus(@Param("instr")Integer instr,@Param("inst")Integer inst, 
			@Param("status")String status, Pageable pageable);
	
	@RestResource(exported = false)
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM Student s WHERE s.id =:studentId AND s.institution.id =:institutionId")
	boolean isRegisteredInTheInstitution(@Param("studentId")Integer studentId, @Param("institutionId")Integer institutionId);
	

}
