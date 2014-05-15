package com.wombling.mobilequiz.pojo;

import java.util.Date;

public class QuestionWithResults {

	private String questionText;
	private boolean currentQuestion;
	private int yesVotes;
	private int noVotes;
	private Date dateTimeCreated;
	private String resourceLink;
	private int secondsRemaining;
	private String id;

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public boolean isCurrentQuestion() {
		return currentQuestion;
	}

	public void setCurrentQuestion(boolean currentQuestion) {
		this.currentQuestion = currentQuestion;
	}

	public int getYesVotes() {
		return yesVotes;
	}

	public void setYesVotes(int yesVotes) {
		this.yesVotes = yesVotes;
	}

	public int getNoVotes() {
		return noVotes;
	}

	public void setNoVotes(int noVotes) {
		this.noVotes = noVotes;
	}

	public Date getDateTimeCreated() {
		return dateTimeCreated;
	}

	public void setDateTimeCreated(Date dateTimeCreated) {
		this.dateTimeCreated = dateTimeCreated;
	}

	public String getResourceLink() {
		return resourceLink;
	}

	public void setResourceLink(String resourceLink) {
		this.resourceLink = resourceLink;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSecondsRemaining() {
		return secondsRemaining;
	}

	public void setSecondsRemaining(int secondsRemaining) {
		this.secondsRemaining = secondsRemaining;
	}

}
