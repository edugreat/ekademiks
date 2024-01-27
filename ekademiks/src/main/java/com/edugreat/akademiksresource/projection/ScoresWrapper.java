package com.edugreat.akademiksresource.projection;

import java.util.List;

//wrapper class for the purpose of providing root object to
// the json object returned when trying to access instance of ScoreAndDate object
public class ScoresWrapper {
	
	private List<ScoreAndDate> scores;

	public ScoresWrapper(List<ScoreAndDate> scores) {
		this.scores = scores;
	}

	public List<ScoreAndDate> getScores() {
		return scores;
	}
	
	

}
