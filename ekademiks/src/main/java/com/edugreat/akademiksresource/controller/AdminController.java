package com.edugreat.akademiksresource.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.contract.AdminInterface;
import com.edugreat.akademiksresource.dto.AppUserDTO;
import com.edugreat.akademiksresource.dto.InstitutionDTO;
import com.edugreat.akademiksresource.dto.LevelDTO;
import com.edugreat.akademiksresource.dto.QuestionDTO;
import com.edugreat.akademiksresource.dto.StudentDTO;
import com.edugreat.akademiksresource.dto.StudentRecord;
import com.edugreat.akademiksresource.dto.SubjectDTO;
import com.edugreat.akademiksresource.dto.TestDTO;
import com.edugreat.akademiksresource.enums.Exceptions;
import com.edugreat.akademiksresource.exception.AcademicException;
import com.edugreat.akademiksresource.service.StatesAndRegionService;
import com.edugreat.akademiksresource.util.ApiResponseObject;
import com.edugreat.akademiksresource.util.AssessmentTopic;
import com.edugreat.akademiksresource.util.AssessmentTopicRequest;
import com.edugreat.akademiksresource.util.Region;
import com.edugreat.akademiksresource.views.UserView;
import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/admins")
@Tag(name = "Admin Management", description = "Endpoints for managing users, assessments, and institutional resources (Requires ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminInterface service;
    private Validator validator;
    private final StatesAndRegionService region;

    @GetMapping("/user")
    @JsonView(UserView.class)
    @Operation(summary = "Get user by email", 
               description = "Retrieves user details by email address. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AppUserDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid email format"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<AppUserDTO> searchByEmail(
            @Parameter(description = "Email address of the user to retrieve", required = true, example = "user@example.com")
            @RequestParam String email) {
        final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (Pattern.matches(emailRegex, email))
            return ResponseEntity.ok(service.searchByEmail(email));

        throw new AcademicException("Invalid email format", Exceptions.BAD_REQUEST.name());
    }

    @GetMapping("/students")
    @JsonView(UserView.class)
    @Operation(summary = "Get all students", 
               description = "Retrieves a list of all registered students. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "List of students retrieved successfully",
                content = @Content(schema = @Schema(implementation = StudentDTO.class)))
    public ResponseEntity<List<StudentDTO>> allStudent() {
        return ResponseEntity.ok(service.allStudents());
    }

    @GetMapping
    @Operation(summary = "Get all admin users", 
               description = "Retrieves a list of all admin users. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "List of admins retrieved successfully")
    public ResponseEntity<Object> admins() {
        return ResponseEntity.ok(service.allAdmins());
    }

    @PutMapping
    @Operation(summary = "Update user password", 
               description = "Updates the password for a user account. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> updatePassword(
            @Parameter(description = "User DTO containing updated password", required = true)
            @RequestBody @Valid AppUserDTO dto) {
        service.updatePassword(dto);
        return ResponseEntity.ok("Updated");
    }

    @DeleteMapping
    @Operation(summary = "Delete user by email", 
               description = "Deletes a user account by email address. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email format"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "Email address of the user to delete", required = true, example = "user@example.com")
            @RequestParam String email) {
        final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (Pattern.matches(emailRegex, email)) {
            service.deleteUser(email);
            return ResponseEntity.ok("Deleted");
        }
        throw new AcademicException("Invalid email format", Exceptions.BAD_REQUEST.name());
    }

    @PostMapping("/subjects")
    @Operation(summary = "Create new subjects", 
               description = "Adds new subjects to the system. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subjects created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid subject data format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> setSubject(
            @Parameter(description = "List of subject DTOs to create", required = true)
            @RequestBody List<SubjectDTO> dtos) {
    	
    
       
    	List<String> violations = validateObjectList(dtos);
    	
    	if(!violations.isEmpty()) {
    		
    		return new ResponseEntity<>(violations, HttpStatus.BAD_REQUEST);
    	}
       
        try {
        	 service.setSubjects(dtos);
        	 return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
    }
    
    @PutMapping("/update/questions")
    @Operation(summary = "Update questions", 
               description = "Updates a list of assessment questions. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Questions updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid question data format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
        @ApiResponse(responseCode = "404", description = "Test or questions not found")
    })
    public ResponseEntity<Object> updateQuestion(
            @Parameter(description = "List of question DTOs with updates", required = true)
            @RequestBody List<QuestionDTO> questionDTOs,
            @Parameter(description = "ID of the test containing the questions", required = true)
            @RequestParam Integer testId) {
        try {
            service.modifyQuestion(questionDTOs, testId);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/test")
    @Operation(summary = "Create assessment", 
               description = "Uploads new assessments to the system. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assessment created successfully",
                    content = @Content(schema = @Schema(implementation = TestDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid assessment data format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> uploadAssessment(
            @Parameter(description = "Test DTO containing assessment details", required = true)
            @RequestBody @Valid TestDTO testDTO) {
        try {
            return new ResponseEntity<Object>(service.uploadAssessment(testDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("categories")
    @Operation(summary = "Get assessment levels", 
               description = "Retrieves all academic assessment levels. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Assessment levels retrieved successfully")
    public ResponseEntity<Object> findAll() {
        return new ResponseEntity<Object>(service.findAllLevels(), HttpStatus.OK);
    }

    @PostMapping("/categories")
    @Operation(summary = "Create assessment categories", 
               description = "Adds new assessment categories. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assessment levels created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid level data format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> addLevel(
            @Parameter(description = "List of level DTOs to create", required = true)
            @RequestBody List<LevelDTO> dtos) {
    	System.out.println("controller");
        try {
        	
        	List<String> violations = validateObjectList(dtos) ;
        	
           if(!violations.isEmpty()) {
        	   
        	   return new ResponseEntity<>(violations, HttpStatus.BAD_REQUEST);
           }
        	
            service.addLevels(dtos);
            
            
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
        	
        	System.out.println(e);
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/test")
    @Operation(summary = "Update assessment test", 
               description = "Updates an existing assessment test (partial update). Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assessment updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid update data format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials"),
        @ApiResponse(responseCode = "404", description = "Assessment not found")
    })
    public ResponseEntity<Object> updateTest(
            @Parameter(description = "Map of fields to update", required = true)
            @RequestBody Map<String, Object> updates,
            @Parameter(description = "ID of the assessment to update", required = true)
            @RequestParam Integer id) {
        try {
            service.updateTest(id, updates);
            
           
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
        	
        	      	
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/welcome")
    @Operation(summary = "Create welcome messages", 
               description = "Creates new welcome messages for students. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Welcome messages created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid message data format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> postWelcome(
            @Parameter(description = "Map of welcome messages by category", required = true)
            @RequestBody Map<String, Collection<String>> welcomeMsg) {
        try {
            service.createWelcomeMessages(welcomeMsg);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/delete")
    @Operation(summary = "Delete student account", 
               description = "Permanently deletes a student account by ID. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student account deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid student ID format"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> deleteStudentAccount(
            @Parameter(description = "ID of the student to delete", required = true, example = "123")
            @RequestParam String studentId) {
        try {
            Integer id = Integer.parseInt(studentId);
            service.deleteStudentAccount(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<Object>("Invalid id", HttpStatus.BAD_REQUEST);
        }
    }
    
    @PatchMapping("/disable")
    @Operation(summary = "Disable student account", 
               description = "Temporarily disables a student account. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student account disabled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid student ID format"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> disableStudentAccount(
            @Parameter(description = "Map containing student ID", required = true,
                      schema = @Schema(example = "{\"studentId\": 123}"))
            @RequestBody Map<String, Integer> map) {
        try {
            final Integer studentId = map.get("studentId");
            service.disableStudentAccount(studentId);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>("Invalid id", HttpStatus.NOT_FOUND);
        }
    }
    
    @PatchMapping("/enable")
    @Operation(summary = "Enable student account", 
               description = "Re-enables a previously disabled student account. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student account enabled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid student ID format"),
        @ApiResponse(responseCode = "404", description = "Student not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> enableStudentAccount(
            @Parameter(description = "Map containing student ID", required = true,
                      schema = @Schema(example = "{\"studentId\": 123}"))
            @RequestBody Map<String, Integer> map) {
        try {
            final Integer studentId = map.get("studentId");
            service.enableStudentAccount(studentId);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/del/question")
    @Operation(summary = "Delete assessment questions", 
               description = "Deletes specific questions from an assessment. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Questions deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid test or question ID"),
        @ApiResponse(responseCode = "404", description = "Test or question not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> deleteQuestion(
            @Parameter(description = "ID of the test containing the question", required = true)
            @RequestParam Integer testId,
            @Parameter(description = "ID of the question to delete", required = true)
            @RequestParam Integer questionId) {
        try {
            service.deleteQuestion(testId, questionId);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PatchMapping("/modify/test")
    @Operation(summary = "Modify assessment", 
               description = "Updates assessment test's topic and duration. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assessment modified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid modification data"),
        @ApiResponse(responseCode = "404", description = "Assessment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> modifyAssessment(
            @Parameter(description = "Map of fields to modify", required = true,
                      schema = @Schema(example = "{\"topic\": \"Math\", \"duration\": 60}"))
            @RequestBody Map<String, Object> modifying,
            @Parameter(description = "ID of the assessment to modify", required = true)
            @RequestParam Integer assessmentId) {
        try {
            service.modifyAssessment(modifying, assessmentId);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/assessment")
    @Operation(summary = "Delete assessment", 
               description = "Permanently deletes an assessment by ID. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assessment deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid assessment ID"),
        @ApiResponse(responseCode = "404", description = "Assessment not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> deleteAssessment(
            @Parameter(description = "ID of the assessment to delete", required = true)
            @RequestParam Integer testId) {
        try {
            service.deleteAssessment(testId);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/topics")
    @Operation(summary = "Get assessment topics", 
               description = "Retrieves all assessment topics for the given category ID category. Requires ADMIN role.")
    @ApiResponse(responseCode = "200", description = "Assessment topics retrieved successfully",
                content = @Content(schema = @Schema(example = "{\"Math\": [\"Algebra\", \"Geometry\"], \"Science\": [\"Biology\", \"Chemistry\"]}")))
    public ResponseEntity<ApiResponseObject<List<AssessmentTopic>>> assessmentTopics(@RequestParam("category") Integer categoryId) {
    	
    	
        try {
        	Map<Integer, String> maps = service.getAssessmentTopics(categoryId);
        	
        	List<AssessmentTopic> assessmentTopics = new ArrayList<>();
        	for(Map.Entry<Integer, String> entry: maps.entrySet()) {
        		
        		assessmentTopics.add(new AssessmentTopic(entry.getKey(), entry.getValue()));
        	}
        	
        	
        	ApiResponseObject<List<AssessmentTopic>> response = new ApiResponseObject<>(assessmentTopics, null, true);
        	
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
            
        } catch (Exception e) {
        	
        	System.out.println(e.getMessage());
        	
        	
        	ApiResponseObject<List<AssessmentTopic>> response = new ApiResponseObject<>(null, e.getMessage(), false);
        	
        	return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PatchMapping("/topics")
    @Operation(summary = "Update assessment topic", 
               description = "Renames an assessment topic within a category. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Topic updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid topic data"),
        @ApiResponse(responseCode = "404", description = "Category or topic not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> updateAssessmentTopic(
            @Parameter(description = "Map containing new topic name", required = true,
                      schema = @Schema(example = "{\"newTopic\": \"Advanced Algebra\"}"))
            @RequestBody AssessmentTopicRequest update) {
    	
    	
        try {
           service.updateAssessmentTopic(update);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
        	
        	System.out.println(e.getMessage());
           return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/topics")
    @Operation(summary = "Delete assessment topic", 
               description = "Deletes an assessment topic from a category. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Topic deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category or topic"),
        @ApiResponse(responseCode = "404", description = "Category or topic not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> deleteAssessment(
            @Parameter(description = "Category containing the topic", required = true)
            @RequestParam Integer assessmentId, Integer category) {
        try {
            service.deleteAssessment(assessmentId,  category);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            return new ResponseEntity<Object>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/subjects")
    @Operation(summary = "Get assessment subjects", 
               description = "Retrieves all assessment subject names. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subjects retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> assessmentSubjectNames() {
        try {
            return new ResponseEntity<Object>(service.assessmentSubjects(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong!", HttpStatus.BAD_REQUEST);
        }
    }
    
    @PatchMapping("/update/subject_name")
    @Operation(summary = "Update subject name", 
               description = "Renames an assessment subject. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject name updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid subject data"),
        @ApiResponse(responseCode = "404", description = "Subject not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> updateSubjectName(
            @Parameter(description = "Map containing new subject name", required = true,
                      schema = @Schema(example = "{\"newName\": \"Advanced Mathematics\"}"))
            @RequestBody Map<String, String> editingObject,
            @Parameter(description = "Current name of the subject", required = true)
            @RequestParam String oldName) {
        try {
            service.updateSubjectName(editingObject, oldName);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/delete/subject")
    @Operation(summary = "Delete subject", 
               description = "Deletes an assessment subject. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subject deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category or subject"),
        @ApiResponse(responseCode = "404", description = "Category or subject not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> deleteSubject(
            @Parameter(description = "Category containing the subject", required = true)
            @RequestParam String category,
            @Parameter(description = "Name of the subject to delete", required = true)
            @RequestParam String subjectName) {
        try {
            service.deleteSubject(category, subjectName);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PatchMapping("/update/categories")
    @Operation(summary = "Update category name", 
               description = "Renames an assessment category. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category name updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category data"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> updateCategoryName(
            @Parameter(description = "New name for the category", required = true)
            @RequestBody String currentName,
            @Parameter(description = "Current name of the category", required = true)
            @RequestParam String previousName) {
        try {
            service.updateCategoryName(currentName, previousName);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/delete/categories")
    @Operation(summary = "Delete category", 
               description = "Deletes an assessment category and all its contents. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid category name"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> deleteCategory(
            @Parameter(description = "Name of the category to delete", required = true)
            @RequestParam String category) {
        try {
            service.deleteCategory(category);
            return new ResponseEntity<Object>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register institution", 
               description = "Creates a new institution account. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Institution registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid institution data"),
        @ApiResponse(responseCode = "409", description = "Institution already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> registerInstitution(
            @Parameter(description = "Institution DTO containing registration details", required = true)
            @RequestBody @Valid InstitutionDTO institutionDTO) {
    	
    
    	try {
    		
    		List<String> violations = validateObject(institutionDTO);
    		
    		if(!violations.isEmpty()) {
    			
    			return new ResponseEntity<>(violations, HttpStatus.BAD_REQUEST);
    		}
            service.registerInstitution(institutionDTO);
            
            return new ResponseEntity<>(HttpStatus.OK);
          
        } catch (Exception e) {
            return new ResponseEntity<Object>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/institutions")
    @Operation(summary = "Get institutions", 
               description = "Retrieves all registered institutions. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Institutions retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid admin ID format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> getInstitutions(
            @Parameter(description = "ID of the admin making the request", required = true)
            @RequestHeader String adminId) {
    	
    	
        try {
            if(adminId != null) {
            	
            	
            	
                return new ResponseEntity<Object>(service.getInstitutions(Integer.parseInt(adminId)), HttpStatus.OK);
            }
        } catch (Exception e) {
        	 System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
           
        }
        return new ResponseEntity<>(new IllegalArgumentException("Number format error"), HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/register_student")
    @Operation(summary = "Register students", 
               description = "Bulk registration of students to an institution. Requires ADMIN role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Students registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid student data"),
        @ApiResponse(responseCode = "404", description = "Institution not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    public ResponseEntity<Object> addStudentRecords(
            @Parameter(description = "List of student records to register", required = true)
            @RequestBody List<StudentRecord> studentRecords,
            @Parameter(description = "ID of the institution to register students to", required = true)
            @RequestHeader String institutionId) {
    	
    	
        try {
            service.addStudentRecords(studentRecords, Integer.parseInt(institutionId));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
        	
        	System.out.println(e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    private <T extends Object> List<String>  validateObjectList(List<T> toValidate) {
    	
    	List<String> errors = new ArrayList<>();
    	
    	toValidate.forEach(dto -> {
    		
    		Set<ConstraintViolation<T>> violations = validator.validate(dto);
    		
    		if(!violations.isEmpty()) {
    			
    			violations.stream().map(error -> error.getMessage()).toList().forEach(e -> errors.add(e));
    			
    		}
    	});
    	
    	return errors;
    	
    	
    }
    
    private <T extends Object> List<String> validateObject(T obj){
    	
    	List<String> errors = new ArrayList<>();
    	
    	Set<ConstraintViolation<T>> violations = validator.validate(obj);
    	if(!violations.isEmpty()) {
    		
    		violations.stream().map(error -> error.getMessage()).toList() .forEach(e -> errors.add(e));
    		
    	}
    	
    	return errors;
    }
    
    @GetMapping("/regions")
    public ResponseEntity<ApiResponseObject<List<Region>>> states() {
      
    	try {
			List<Region> states = region.getAllStates();
			
			ApiResponseObject<List<Region>> response = new ApiResponseObject<List<Region>>(states, null, true);
		
			return new ResponseEntity<>(response, HttpStatus.OK);
    	
    	} catch (Exception e) {
			
    		ApiResponseObject<List<Region>> response = new ApiResponseObject<>(null, e.getMessage(), false);
		
    		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    	}
    	
    	
    }
    
    @GetMapping("/lgas")
    public ResponseEntity<ApiResponseObject<List<String>>> lgas(@RequestParam String state) {
       
    	try {
			
    		 
        	List<String> lgas = region.getLGAsByState(state);
        	
        	ApiResponseObject<List<String>> response = new ApiResponseObject<List<String>>(lgas, null, true);
        	
        	return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			
			ApiResponseObject<List<String>> response = new ApiResponseObject<>(null, e.getMessage(), false);
			
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
    }
    
    
     @GetMapping("/assessment/names")
    public ResponseEntity<ApiResponseObject<List<String>>> getAssessmentNames(@RequestHeader List<String> studentTestIds) {
       
    	try {
    		
    		List<Integer> ids = studentTestIds.stream().map(Integer::parseInt).toList();
    		
			List<String> assessmentNames = service.getAssessmentNamesFor(ids);
			
			ApiResponseObject<List<String>> response = new ApiResponseObject<List<String>>(assessmentNames, null, true);
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
		
			ApiResponseObject<List<String>> response = new ApiResponseObject<List<String>>(null, e.getMessage(), false);
			
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			
		}
    	
    
    }
    
    
    
    
    
}

 