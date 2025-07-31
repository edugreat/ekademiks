package com.edugreat.akademiksresource.config;

// parameters for redis values to avoid errors due to hard coding
public class RedisValues {
	
	public static final String USER_CACHE = "USER_CACHE";
	
	public static final String CURRENT_ROLE = "CURRENT_ROLE";
	
	public static final String SUBJECT_NAMES = "subjectNames";
	
	public static final String RECENT_PERFORMANCE = "recent_performance"; 
	
	public static final String TOPICS_AND_DURATIONS = "topics_and_durations";
	
	public static final String TOPIC_AND_DURATION = "topic_and_duration";
	
	public static final String ASSESSMENT_TEST = "assessment_test";
	
	public static final String JOIN_DATE = "join_date"; //caches when a user joins a group chat

//	caches all institutions a particular (admin) user has registered
	public static final String MY_INSTITUTIONS = "my_institutions";

	public static final String WELCOME_MSG = "welcome_msg";

	public static final String ASSESSMENT_TOPICS = "assessment_topics";
	
	public static final String ASSIGNMENT_DETAILS = "assignment_details";
	
//	caches the records of responses to assignment/assessment for easy notifications to instructors
	public static final String ASSESSMENT_RESPONSE_NOTIFICATION = "assessment_response_cache";
	
	public static final String MY_GROUP = "myGroup";
	
	public static final String IS_GROUPMEMBER =  "isGroupMember";
	
	public static final String PREVIOUS_CHATS = "previousChats";
	
	public static final String ALL_GROUPS = "allGroups";
	
	public static final String MY_GROUP_IDs =  "myGroupIds";
	
	public static final String MISCELLANEOUS = "miscellaneous";
	
	public static final String PENDING_REQUEST  = "pendingRequest";
	public static final String GROUP_CHAT_INFO = "groupChatInfo";
}
