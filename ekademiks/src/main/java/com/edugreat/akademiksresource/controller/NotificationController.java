package com.edugreat.akademiksresource.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.dto.NotificationRequestDTO;
import com.edugreat.akademiksresource.service.NotificationService;



@RestController
@RequestMapping("/admins/notify")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	
	@PostMapping
	public ResponseEntity<Object> postMethodName(@RequestBody NotificationRequestDTO notificationDTO) {
		
		if(notificationDTO != null) {
			
			notificationService.postNotification(notificationDTO);
			
			return new ResponseEntity<>(HttpStatus.OK);
			
			
		}
		
		
		
		
		throw new IllegalArgumentException("Something went wrong!");
	}
	
	
	

	
	
}
