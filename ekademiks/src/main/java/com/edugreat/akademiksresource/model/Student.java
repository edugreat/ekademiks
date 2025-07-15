
package com.edugreat.akademiksresource.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.edugreat.akademiksresource.chat.model.GroupMember;
import com.edugreat.akademiksresource.enums.Roles;
import com.edugreat.akademiksresource.instructor.Instructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table
//this is a subclass of the AppUser base class
public class Student extends AppUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ElementCollection(fetch = FetchType.EAGER)
	private Set<UserRoles> roles = new HashSet<>();
	
//	keeps a collection of the groups the student has requested to join which are yet to get approved
	@JsonIgnore
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<Integer> pendingGroupChatRequests = new HashSet<>();
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "institution_id", nullable = true)
	@JsonBackReference
	private Institution institution;
	
	@ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Instructor> instructors = new HashSet<>();
	
//	status depicts student's academic status such as SENIOR or JUNIOR
	@Column(nullable = false)
	String status;

	public Student() {
		super();

	}

	public Student(String firstName, String lastName, String email, String mobileNumber, String password) {
		super(firstName, lastName, email, mobileNumber, password);

	}

	public Student(String firstName, String lastName, String email, String password) {
		super(firstName, lastName, email, password);

	}
    
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, mappedBy = "student", orphanRemoval = true)
	@JsonIgnore
	private Set<StudentTest> studentTests = new HashSet<>();

	//	A set of assessmentUploadNotifications a student receives
	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@JoinTable(name = "student_notifications", joinColumns = @JoinColumn(name = "student_id"),  inverseJoinColumns = @JoinColumn(name = "notification_id"))
	private Set<AssessmentUploadNotification> assessmentNotifications = new HashSet<>();
	
	@JsonIgnore
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@JoinTable(name = "student_miscellaneous_notification", joinColumns = @JoinColumn(name = "student_id"), inverseJoinColumns= @JoinColumn(name = "notification_id"))
	private Set<MiscellaneousNotifications> miscellaneousNotices = new HashSet<>();
	
	
	
	
	public Set<MiscellaneousNotifications> getMiscellaneousNotices() {
		return miscellaneousNotices;
	}

	public void setMiscellaneousNotices(Set<MiscellaneousNotifications> miscellaneousNotices) {
		this.miscellaneousNotices = miscellaneousNotices;
	}

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<GroupMember> groupMembers = new ArrayList<>();
	
//	a collection representing the details about unread chat messages, where key is the group id and value is the number of unread messages
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable
	@MapKeyColumn(name = "group_id")
	@Column(name = "unreadChats", nullable = true)
	@JsonIgnore
	private SortedMap<Integer, Integer> unreadChats = new TreeMap<>();
	
     	               
	
	public SortedMap<Integer, Integer> getUnreadChats() {
		return unreadChats;
	}
	
	

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name  = "assignment_response",
	joinColumns = @JoinColumn(name = "student_id"))
	private List<AssignmentResponse> assignmentResponses = new ArrayList<>();


	// convenience method
	public void addStudentTest(StudentTest studentTest) {

		if (studentTest != null) {
			this.studentTests.add(studentTest);
		}
	}

	public Set<AssessmentUploadNotification> getAssessmentNotifications() {
		return assessmentNotifications;
	}

	//     Convenience method to add notification to student
	public void AddNotification(AssessmentUploadNotification notication) {

		if (!assessmentNotifications.contains(notication)) {

			assessmentNotifications.add(notication);
		}
	}
	
	public void addMiscellaneousNotices(MiscellaneousNotifications notices) {
		
		if(! this.miscellaneousNotices.contains(notices)) {
			
			this.miscellaneousNotices.add(notices);
		}
	}


	public Set<Integer> getPendingGroupChatRequests() {
		return pendingGroupChatRequests;
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return List.of(new SimpleGrantedAuthority(Roles.Student.name()));
	}

	public Set<StudentTest> getStudentTests() {
		return studentTests;
	}

	public void setStudentTests(Set<StudentTest> studentTests) {
		this.studentTests = studentTests;
	}

	public Set<String> getRoles() {
		return roles.stream().map(role -> role.getRole().toString()).collect(Collectors.toSet());
	}

	public void setRoles(Set<UserRoles> roles) {
		this.roles = roles;
	}

	public void addRoles(Set<String> roles) {
		for (String role : roles) {
			this.roles.add(new UserRoles(Roles.valueOf(role)));
		}
	}

	//	Removes student roles
	public void removeRole(String role) {

		getRoles().remove(role);
	}

	public List<GroupMember> getGroupMembers(){
		
		return this.groupMembers;
	}
	
//	adds to the collection of unread chats, new chats.
	public void addUnreadChat(Map<Integer, Integer> unreadChat) {
		
		
		unreadChat.forEach((groupId, _unreadChats) -> {
			
//			if the user already has some unread chats for the given group id, simply increase the number of unread chats
			if(unreadChats.containsKey(groupId)) {
				
//				
				unreadChats.put(groupId, unreadChats.get(groupId) + _unreadChats);
				
			}else {
//				add a new key-value record of unread chats to the user's unreadChats collection if they do not have previous unread chats records for the given group chat
				
				unreadChats.put(groupId, _unreadChats);
			}
		}) ;
	}
	
//	add the group chat id to the collection of group chat ids the student has requested to join, but have yet to be approved
	public void addToPendingGroupChatRequests(Integer groupChatId) {
		
		if(! pendingGroupChatRequests.contains(groupChatId)) {
			
			pendingGroupChatRequests.add(groupChatId);
		}
		
		
		
	}

	public Institution getInstitution() {
		
		return institution;
	}
	
	public void setInstitution(Institution institution) {
		
		this.institution = institution;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<AssignmentResponse> getAssignmentResponses() {
		return assignmentResponses;
	}

	public Set<Instructor> getInstructors() {
		// TODO Auto-generated method stub
		return instructors;
	}
	
	

}


