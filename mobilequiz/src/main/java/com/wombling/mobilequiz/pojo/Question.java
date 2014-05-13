package com.wombling.mobilequiz.pojo;

public class Question {

	private String questionText;
	private int secondsRemaining;
	private String responseUrl;
	private String id;

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public int getSecondsRemaining() {
		return secondsRemaining;
	}

	public void setSecondsRemaining(int secondsRemaining) {
		this.secondsRemaining = secondsRemaining;
	}

	public String getResponseUrl() {
		return responseUrl;
	}

	public void setResponseUrl(String responseUrl) {
		this.responseUrl = responseUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
