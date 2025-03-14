package com.edugreat.akademiksresource.assignment;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/assignments")
public class AssignmentController {
	
	@Autowired
	private AssignmentInterface _interface;
	
	
	
//	post assignment details along with pdf 
	@PostMapping(path = "/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<Object> postAssignment(@RequestPart("details") AssignmentDetailsDTO details, 
			@RequestPart("pdf") MultipartFile[] files) {
		
		
		
		
		Integer detailsId  = null;
		
		
		
		try {
			
			
			
			detailsId = _interface.setAssignment(details, processFiles(files));
		} catch (Exception e2) {
			
			return new ResponseEntity<Object>(e2.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}
		
		
		return new ResponseEntity<Object>(detailsId, HttpStatus.OK);
	}
	
//	helper method that processes the assignment files
	private Set<AssignmentPdfDTO> processFiles(MultipartFile[] files) throws IOException{
		
		Set<AssignmentPdfDTO> pdfs = new HashSet<>();
		
		for(MultipartFile file: files) {
			
			AssignmentPdfDTO pdf = new AssignmentPdfDTO(
					file.getOriginalFilename(),
					file.getContentType(),
					file.getBytes()
					);
			
			pdfs.add(pdf);
			
		}
		
		
		return pdfs;
		
		
	}
	
//	post assignment with no pdf
	@PostMapping
	public ResponseEntity<Object> postAssignment(@RequestBody @Valid AssignmentDetailsDTO details) {
		
		Integer detailsId = null;
		
		try {
			
			detailsId = _interface.setAssignment(details);
			
		} catch (Exception e) {
			
			return new ResponseEntity<Object>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}
		
		
		return new ResponseEntity<Object>(detailsId, HttpStatus.OK);
	}
	
	
	

}
