package com.wombling.mobilequiz.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wombling.mobilequiz.user.QuestionService;

@Service
public class WebSocketSupportBean {

	@Autowired
	QuestionService qs;

	private volatile static WebSocketSupportBean instance = null;

	public static WebSocketSupportBean getInstance() {
		return instance;
	}

	public WebSocketSupportBean() {
		instance = this;
	}

	public QuestionService getQs() {
		return qs;
	}
}
