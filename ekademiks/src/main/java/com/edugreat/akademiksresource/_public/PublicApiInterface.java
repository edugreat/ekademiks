package com.edugreat.akademiksresource._public;

import java.util.List;

import com.edugreat.akademiksresource.util.Region;

public interface PublicApiInterface {
	
	List<Region> loadRegions();
	
	List<String> loadLGAs(String state);
	

}
