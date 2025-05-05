package com.edugreat.akademiksresource.assessment.response.notification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentResponseService implements AssignmentResponseInterface {

	private final AssignmentResponseBroadcaster broadcaster;
	private final AssessmentResponseConnector connectors;

	private final AssignmentDetailsDao assignmentDetailsDao;

	private final CacheManager cacheManager;

	private final StudentDao studentDao;
	private final CachingKeysUtil cachingKeyUtils;
	private final RedissonClient redissonClient;
	private final ObjectMapper mapper;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void processAssignmentResponse(AssignmentResponseObj response, String type, Integer detailsId) {

		 //cacheManager.getCache(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION).clear();
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

					assignmentDetails.getTotalQuestions());

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

						assignmentDetails.getTotalQuestions());

//			Theory type PDF assignment
			} else if (assignmentType.contains("pdf_the")) {

//				TODO: Implementation on the way ...
				break;
			}

		case "theory":

//			TODO: Implementation coming ...
			break;
		}

//		TODO: In later version, persist student's assignment responses to redis store.

		 student.getAssignmentResponses().add(new
		 AssignmentResponse(assignmentDetails.getId(),
		 score, instructorId,
		 assignmentDetails.getCreationDate().toLocalDate(),
		 assignmentDetails.getName()));
		 
		 studentDao.save(student);
		 
//		cache assignment response details for easy notifications to s
		Cache cache = cacheManager.getCache(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION);

		if (cache.get(instructorId) == null) {
			
			

			_record = AssessmentResponseRecord.builder().topic(assignmentDetails.getName())
					.postedOn(assignmentDetails.getCreationDate().toLocalDate()).respondedOn(LocalDate.now())
					.studentId(response.getStudentId()).instructorId(instructorId).build();

			Set<AssessmentResponseRecord> notifications = new HashSet<>();
			log.info("about to broadcast 1: {}", _record);
			final boolean added = notifications.add(_record);
			if (added) {

				cache.put(instructorId, notifications);

//				Send instant notification to instructor...
				broadcaster.broadcastInstantNotification(_record);

			}

		} else {

//				extract previous notifications into a set	
			Set<AssessmentResponseRecord> _records = ((ArrayList<AssessmentResponseRecord>) cache.get(instructorId)
					.get()).stream().collect(Collectors.toSet());

			log.info("about to broadcast 2: {}", _record);
//	add to the list of records already cached
			final boolean added = _records.add(new AssessmentResponseRecord(assignmentDetails.getName(),
					assignmentDetails.getCreationDate().toLocalDate(), LocalDate.now(), response.getStudentId(),
					instructorId));

			if (added) {

				cache.put(instructorId, _records);

//				Send instant notification to instructor...

				broadcaster.broadcastInstantNotification(_record);

			}

		}

	}

	private double computeScore(List<String> responses, List<String> answers, double totalMark, int totalQuestions) {

		double score = 0.0;

//		round to the nearest 2d.p
		final double markPerQuestion = BigDecimal.valueOf(totalMark / totalQuestions).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();

		for (int i = 0; i < responses.size(); i++) {

			if (responses.get(i).equals(answers.get(i)))
				score += markPerQuestion;

		}

//		round to the nearest 2d.p
		return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP).doubleValue();

	}

	@Override
	public Set<AssessmentResponseRecord> getPreviousResponses(Integer instructorId) {

		Cache cache = cacheManager.getCache(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION);

//		get previously cached keys
		final Optional<Integer> cachedKey = cachingKeyUtils
				.getAllCacheKeys(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION).stream()
				.filter(x -> x.equals(String.valueOf(instructorId))).map(Integer::parseInt).findFirst();
		try {

			if (cachedKey.isPresent()) {

				Cache.ValueWrapper valueWrapper = cache.get(cachedKey);

				if (valueWrapper != null) {

					Set<AssessmentResponseRecord> notifications = mapper.convertValue(valueWrapper.get(),

							new TypeReference<Set<AssessmentResponseRecord>>() {
							});

					return notifications;

				}

			} else {

//				scan the database for notifications
				List<Student> students = studentDao.getAssessmentResponses(instructorId);

				Set<AssessmentResponseRecord> notifications = new HashSet<>();

				for (Student s : students) {

					for (AssignmentResponse r : s.getAssignmentResponses()) {

						notifications.add(

								new AssessmentResponseRecord(r.getTopic(), r.getPostedOn(), r.getSubmittedOn(),

										s.getId(),

										r.getInstructorId()));

					}
				}

//				cache the notifications
				cache.put(instructorId, notifications);
				return notifications;

			}

		} catch (Exception e) {

		}

		return new HashSet<AssessmentResponseRecord>();

	}

	@Scheduled(fixedRate = 60000) // schedule notifications at 60sec (1 minute) interval
	public void scheduleFixedTimeNotification() {

		RLock rlock = redissonClient.getLock("notification:lock");
		try {

			if (rlock.tryLock(5, 15, TimeUnit.SECONDS)) {

				try {
					getCachedAsssessmentResponseNotifications();
				} finally {
					rlock.unlock();
				}
			}
		} catch (Exception e) {
			Thread.currentThread().interrupt();
			System.err.println(e);
		}

	}

//	get cached assessment notifications for scheduled notifications
	private void getCachedAsssessmentResponseNotifications() {
		Set<String> keys = cachingKeyUtils.getAllCacheKeys(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION);

		Cache cache = cacheManager.getCache(RedisValues.ASSESSMENT_RESPONSE_NOTIFICATION);

		Set<Integer> connectedIds = connectors.getConnectedInstructorsId();

//		key represents the assessment instructor's ID
		for (String key : keys) {

//			send previous broadcasts to only instructors still connected
			if (connectedIds.contains(Integer.parseInt(key))) {
				try {

					Cache.ValueWrapper wrapper = cache.get(key);

					if (wrapper != null) {

//									
						Set<AssessmentResponseRecord> notifications = mapper.convertValue(wrapper.get(),

								new TypeReference<Set<AssessmentResponseRecord>>() {
								});

						broadcaster.broadcastPreviousNotifications(notifications);

					}

				} catch (Exception e) {
					System.err.println(e);
				}
			}

		}
	}

}
