package com.edugreat.akademiksresource.util;

import java.util.List;

public class Region {
	
	private String name;
	
	private List<String> lgas;
	
	public Region() {}

	public Region(String name, List<String> lgas) {
		
		this.name = name;
		this.lgas = lgas;
	}

	public String getName() {
		return name;
	}

	public List<String> getLgas() {
		return lgas;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLgas(List<String> lgas) {
		this.lgas = lgas;
	}
	
	
	
	

}
