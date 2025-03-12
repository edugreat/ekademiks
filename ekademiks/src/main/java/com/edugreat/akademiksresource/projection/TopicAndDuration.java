package com.edugreat.akademiksresource.projection;

//projection for fetching the test topics and durations
public record TopicAndDuration(String testName, Long duration) {}
