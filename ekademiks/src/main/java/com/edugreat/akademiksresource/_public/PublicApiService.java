package com.edugreat.akademiksresource._public;

import java.util.List;

import org.springframework.stereotype.Service;

import com.edugreat.akademiksresource.service.StatesAndRegionService;
import com.edugreat.akademiksresource.util.Region;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PublicApiService implements PublicApiInterface {
	
	 private final StatesAndRegionService region;

	@Override
	public List<Region> loadRegions() {
		
		return region.getAllStates();
	}

	@Override
	public List<String> loadLGAs(String state) {
		
		return region.getLGAsByState(state);
	}

}
