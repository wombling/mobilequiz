package com.wombling.mobilequiz.persistance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "QUESTIONS")
public class StoredQuestion {

	@Id
	@Column(name = "QUESTION_ID", length = 200, nullable = true)
	private String id;

	@Column(name = "QUESTION_TEXT", length = 500, nullable = true)
	private String text;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TIME_CREATED")
	private Date timeCreated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EXPIRES")
	private Date expires;

	@Column(name = "CURRENT_QUESTION")
	private boolean current;
	
	@Column(name = "YES_VOTES")
	private int yesVotes;
	
	@Column(name = "NO_VOTES")
	private int noVotes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
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

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

}
