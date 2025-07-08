package com.edugreat.akademiksresource.service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.util.Region;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import jakarta.annotation.PostConstruct;

@Service
public class StatesAndRegionService {

    
	
	private  ObjectMapper mapper = new ObjectMapper();
	
	private List<Region> states;
	
	public StatesAndRegionService() {}


	
	
	@PostConstruct
	public void init() {
		
		try {
			
			InputStream is = getClass().getResourceAsStream("/nigeria/states.json");
			
			states = mapper.readValue(is, new TypeReference<List<Region>>() {});
			
		}catch (Exception e) {
			
			throw new RuntimeException("Failed to load states", e);
		}
	}
	
	public List<Region> getAllStates(){
		
		return states;
	}
	
	public List<String> getLGAsByState(String state){
		
		List<String> states = this.states.stream()
				              .filter(s -> s.getName().equalsIgnoreCase(state))
				              .findFirst()
				              .map(Region::getLgas)
				              .orElse(Collections.emptyList());
		
		Collections.sort(states);
				              
		
		
		return states;
	}
	

}
