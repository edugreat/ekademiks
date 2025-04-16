package com.edugreat.akademiksresource.assessment.response.notification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.assignment.AssignmentDetails;
import com.edugreat.akademiksresource.assignment.AssignmentDetailsDao;
import com.edugreat.akademiksresource.assignment.AssignmentPDF;
import com.edugreat.akademiksresource.assignment.Objectives;
import com.edugreat.akademiksresource.config.RedisValues;
import com.edugreat.akademiksresource.dao.StudentDao;
import com.edugreat.akademiksresource.model.AssignmentResponse;
import com.edugreat.akademiksresource.model.Student;
import com.edugreat.akademiksresource.util.CachingKeysUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class AssignmentResponseService implements AssignmentResponseInterface {

	private final AssignmentResponseBroadcaster broadcaster;

	private final AssignmentDetailsDao assignmentDetailsDao;

	private final CacheManager cacheManager;

	private final StudentDao studentDao;
	private final CachingKeysUtil cachingKeyUtils;
	private final RedissonClient redissonClient;
	private final ObjectMapper mapper;

	public AssignmentResponseService(AssignmentResponseBroadcaster broadcaster,
			AssignmentDetailsDao assignmentDetailsDao, CacheManager cacheManager, 
			StudentDao studentDao, CachingKeysUtil cachingKeyUtils,
			RedissonClient redissonClient, ObjectMapper mapper) {
		this.broadcaster = broadcaster;
		this.assignmentDetailsDao = assignmentDetailsDao;
		this.cacheManager = cacheManager;
		this.studentDao = studentDao;
		this.cachingKeyUtils = cachingKeyUtils;
		this.redissonClient = redissonClient;
		this.mapper = mapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void processAssignmentResponse(AssignmentResponseObj response, String type, Integer detailsId) {

		cacheManager.getCache(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION).clear();
		
//		confirm the existence of the assignment in the database
		AssignmentDetails assignmentDetails = assignmentDetailsDao.findById(detailsId)
				.orElseThrow(() -> new IllegalArgumentException("No such assignment found"));

//	get the student that did the assignment
		Student student = studentDao.findById(response.getStudentId())
				.orElseThrow(() -> new IllegalArgumentException("No record was found for you"));

		Integer instructorId = assignmentDetails.getInstructor().getId();
		
		double score = 0.0;

		AssessmentResponseRecord _record = null;
		
//		fetch the assignment resource from the database
		switch (type.toLowerCase()) {

		case "objectives":

			Set<Objectives> objectives = assignmentDetailsDao.getAssignment(detailsId);

			score = computeScore(response.getSelectedOptions(),
//					 extract answers
					objectives.stream().map(o -> o.getAnswer()).collect(Collectors.toList()),
					assignmentDetails.getAllocatedMark(), 
					
					assignmentDetails.getTotalQuestions()
					);

			break;

		case "pdf":
			Set<AssignmentPDF> pdfs = assignmentDetailsDao.getAssignment(detailsId);

			final String assignmentType = assignmentDetails.getType().toLowerCase();

//			Objectives type PDF assignment
			if (assignmentType.contains("pdf_obj")) {

				score = computeScore(response.getSelectedOptions(),
//						extract answers
						pdfs.stream().map(pdf -> pdf.getAnswer()).collect(Collectors.toList()),
						assignmentDetails.getAllocatedMark(), 
						
						assignmentDetails.getTotalQuestions()
						);

//			Theory type PDF assignment
			} else if (assignmentType.contains("pdf_the")) {

//				TODO: Implementation on the way ...
				break;
			}

		case "theory":

//			TODO: Implementation coming ...
			break;
		}

		//student.getAssignmentResponses().add(new AssignmentResponse(assignmentDetails.getId(),
				//score, instructorId,
				//assignmentDetails.getCreationDate().toLocalDate(), assignmentDetails.getName()));

//		cache assignment response details for easy notifications to s
		Cache cache = cacheManager.getCache(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION);


		if (cache.get(instructorId) == null) {

			 _record = new AssessmentResponseRecord(assignmentDetails.getName(),
					assignmentDetails.getCreationDate().toLocalDate(), LocalDate.now(), response.getStudentId(), instructorId);

			 List<AssessmentResponseRecord> notifications = new ArrayList<>();
			 notifications.add(_record);		 
			cache.put(instructorId,notifications);

		} else {

//			add to the list of records already cached
			List<AssessmentResponseRecord> _records = (ArrayList<AssessmentResponseRecord>) cache.get(instructorId);

			_records.add(new AssessmentResponseRecord(assignmentDetails.getName(),
					assignmentDetails.getCreationDate().toLocalDate(), LocalDate.now(), response.getStudentId(), instructorId));

			cache.put(instructorId, _records);



		}
		
		
//		TODO: Initiate network connection
		
		
//		Send instant notification to instructor...
		broadcaster.broadcastInstantNotification(_record);

	}

	private double computeScore(List<String> responses, List<String> answers, double totalMark, int totalQuestions) {

		double score = 0.0;
		
//		round to the nearest 2d.p
		final double markPerQuestion = BigDecimal.valueOf(totalMark/totalQuestions)
				                                  .setScale(2, RoundingMode.HALF_UP)
				                                  .doubleValue();

		for (int i = 0; i < responses.size(); i++) {

			if (responses.get(i).equals(answers.get(i)))
				score += markPerQuestion;

		}

//		round to the nearest 2d.p
		return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP).doubleValue();

	}

	@Override
	@Cacheable(value = RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION, key = "#instructorId")
	public List<AssessmentResponseRecord> getPreviousResponses(Integer instructorId) {
		
		List<Student> students = studentDao.getAssessmentResponses(instructorId);
		
		List<AssessmentResponseRecord> notifications = new ArrayList<>();
		
		for(Student s: students) {
			
			for(AssignmentResponse r: s.getAssignmentResponses()) {
				
				notifications.add(
						
						new AssessmentResponseRecord(
								r.getTopic(),
								r.getPostedOn(),
								r.getSubmittedOn(), 
								
								s.getId(), 

								r.getInstructorId()));
				
				
			}
		}
		
		
		
		return notifications;
	}
	
	@Scheduled(fixedRate = 100000) //schedule notifications at 20sec interval
	public void scheduleFixedTimeNotification() {
		
	
		
		RLock rlock = redissonClient.getLock("notification:lock");
		try {
			
			if(rlock.tryLock(5, 15, TimeUnit.SECONDS)) {
				
				
				
				try {
					Set<String> keys = cachingKeyUtils.getAllCacheKeys(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION);
					
					Cache cache = cacheManager.getCache(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION);
					
					
					
					for(String key: keys) {
						
						
						try {
							
							
							Cache.ValueWrapper wrapper = cache.get(key);
							
							if(wrapper != null) {
								
//								
								List<AssessmentResponseRecord> notifications = mapper.convertValue(wrapper.get(),
										
										new TypeReference<List<AssessmentResponseRecord>>() {}
										);
								
								broadcaster.broadcastPreviousNotifications(notifications);
								
								
								
							
							}
							
		
						
						} catch (Exception e) {
							System.err.println(e);
						}
					}
				} finally  {
					rlock.unlock();
				}
			}
		} catch (Exception e) {
			Thread.currentThread().interrupt();
			System.err.println(e);
		}
		
		
		}
		
		
	
	}

