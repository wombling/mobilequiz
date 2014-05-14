package com.wombling.mobilequiz.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import com.google.gson.JsonSyntaxException;
import com.wombling.mobilequiz.api.ApiValues;
import com.wombling.mobilequiz.exceptions.AlreadyVotedException;
import com.wombling.mobilequiz.exceptions.NoSessionCookieFoundException;
import com.wombling.mobilequiz.exceptions.QuestionNotValidException;
import com.wombling.mobilequiz.pojo.QuestionResponse;

@Controller
@RequestMapping(value = { ApiValues.RESPONSE, ApiValues.RESPONSE + "/**" })
public class ResponseServlet {

	@Autowired
	QuestionService questionService;

	@RequestMapping(method = RequestMethod.OPTIONS)
	public void doOptions(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		AllowCORS.addCORSHeaders(request, response, "POST");
	}

	@RequestMapping(method = RequestMethod.POST)
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AllowCORS.addCORSHeaders(request, response, "POST");
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		QuestionResponse questionResponse = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					request.getInputStream()));

			questionResponse = gson.fromJson(reader, QuestionResponse.class);

			String userId = getUserIdFromCookies(request);

			questionService.registerQuestionResponse(questionResponse, userId);

			response.getWriter().println(gson.toJson(questionResponse));
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-transform"); // needed for
																	// chrome
																	// data
																	// compression

		} catch (QuestionNotValidException e) {

			response.sendError(403,
					"Response to question was not sent within valid time");
		} catch (NoSessionCookieFoundException e) {
			response.sendError(403, "Response must use session cookie");
		} catch (JsonSyntaxException e) {
			response.sendError(400, "Response not in correct format");
		} catch (AlreadyVotedException e) {
			response.sendError(403, "Only allowed to vote once");
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
