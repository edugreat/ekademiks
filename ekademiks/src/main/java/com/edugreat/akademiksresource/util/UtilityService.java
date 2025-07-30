package com.edugreat.akademiksresource.util;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.lang.Collections;

@Service
public class UtilityService {
	
	public <T> Page<T> convertListToPage(List<T> list, int page, int pageSize){
		int totalElements  = list.size();
		int totalPages = (int)Math.ceil((double)totalElements/pageSize);
		
		if(page < 0 || (page > 0 && page >= totalPages)) {
			
			throw new IllegalArgumentException("Invalid page number");
			
			
		}
		
		int fromIndex = page * pageSize;
		int toIndex = Math.min(fromIndex + pageSize, totalElements);
		
//		create page with only the relevant elements
		List<T> pageContent = list.subList(fromIndex, toIndex);
		
		return new PageImpl<>(pageContent, PageRequest.of(page, pageSize),totalElements);
	}
	
	public <T> Page<T> emptyPage(int page, int size){
		
		
		
		return new  PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
	}

}
