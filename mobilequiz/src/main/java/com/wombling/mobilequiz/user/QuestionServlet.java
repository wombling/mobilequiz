package com.wombling.mobilequiz.user;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wombling.mobilequiz.api.ApiValues;
import com.wombling.mobilequiz.exceptions.NoCurrentQuestionException;
import com.wombling.mobilequiz.exceptions.NoSessionCookieFoundException;
import com.wombling.mobilequiz.pojo.Question;

@Controller
@RequestMapping(ApiValues.GET_QUESTION)
public class QuestionServlet {

	@Autowired
	QuestionService questionService;

	@RequestMapping(method = RequestMethod.OPTIONS)
	public void doOptions(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		AllowCORS.addCORSHeaders(request, response, "GET");
	}

	@RequestMapping(method = RequestMethod.GET)
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		AllowCORS.addCORSHeaders(request, response, "GET");

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String myURL = request.getRequestURL().toString();

		String rootUrl = myURL.substring(0,
				myURL.lastIndexOf(ApiValues.GET_QUESTION));

		try {
			getUserIdFromCookies(request);
		} catch (NoSessionCookieFoundException e1) {

			// create a session cookie for this user
			String guid = UUID.randomUUID().toString();
			Cookie userIdCookie = new Cookie(ApiValues.USER_COOKIE, guid);
			response.addCookie(userIdCookie);
		}

		try {
			Question question = questionService.getCurrentQuestion(rootUrl);

			response.getWriter().println(gson.toJson(question));
			response.setContentType("application/json");

		} catch (NoCurrentQuestionException e) {

			response.sendError(404, "No Current Question");
		}

	}

	private String getUserIdFromCookies(HttpServletRequest request)
			throws NoSessionCookieFoundException {
		Cookie[] cookies = request.getCookies();

		String userId = null;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(ApiValues.USER_COOKIE)) {
					userId = cookies[i].getValue();
				}
			}
		}
		if (userId == null) {
			throw new NoSessionCookieFoundException();
		}
		return userId;
	}
}
