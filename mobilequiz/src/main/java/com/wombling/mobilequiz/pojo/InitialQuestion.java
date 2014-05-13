package com.wombling.mobilequiz.pojo;

public class InitialQuestion {
	String questionText;
	int secondsUntilExpiry;
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public int getSecondsUntilExpiry() {
		return secondsUntilExpiry;
	}
	public void setSecondsUntilExpiry(int secondsUntilExpiry) {
		this.secondsUntilExpiry = secondsUntilExpiry;
	}

}
