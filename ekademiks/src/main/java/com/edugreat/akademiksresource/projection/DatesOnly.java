package com.edugreat.akademiksresource.projection;

import java.sql.Date;
import java.time.LocalDate;

/*
 * This is an interface for projection purpose.
 * It declared a method to return just the dates attributes of the subject entity
 */

public interface DatesOnly {
	
	LocalDate getExamYear();
	 
	 
	 
	
}
