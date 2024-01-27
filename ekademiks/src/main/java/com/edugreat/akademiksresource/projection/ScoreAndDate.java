package com.edugreat.akademiksresource.projection;

import java.time.LocalDateTime;


//this is a projection for the StudentTest class that returns two of
//the fields in the entity
public interface ScoreAndDate {
	
	//returns the score the student got
	double getScore();
	
	//returns when the test was taken
	public LocalDateTime getWhen();

}
