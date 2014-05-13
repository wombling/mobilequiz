package com.wombling.mobilequiz.persistance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RESPONSES")
public class Responses {

	@Id
	@Column(name = "QUESTION_ID", length = 200, nullable = true)
	private String id;

	@Column(name = "USER_ID", length = 80, nullable = true)
	private String userId;

	@Column(name = "RESPONSE", length = 3, nullable = true)
	private String response;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

}
