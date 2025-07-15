package com.edugreat.akademiksresource._public;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edugreat.akademiksresource.util.ApiResponseObject;
import com.edugreat.akademiksresource.util.Region;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/public")
@AllArgsConstructor
public class PublicApiController {
	
	private final PublicApiInterface publicInterface;
	
	
	
	
	@GetMapping("/regions")
    public ResponseEntity<ApiResponseObject<List<Region>>> states() {
      
    	try {
			List<Region> states = publicInterface.loadRegions();
			
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
			
    		 
        	List<String> lgas = publicInterface.loadLGAs(state);
        	
        	ApiResponseObject<List<String>> response = new ApiResponseObject<List<String>>(lgas, null, true);
        	
        	return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			
			ApiResponseObject<List<String>> response = new ApiResponseObject<>(null, e.getMessage(), false);
			
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
    }
    

}
