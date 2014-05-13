package com.wombling.mobilequiz.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wombling.mobilequiz.api.ApiValues;
import com.wombling.mobilequiz.pojo.QuestionList;
import com.wombling.mobilequiz.user.AllowCORS;
import com.wombling.mobilequiz.user.QuestionService;

@Controller
@RequestMapping(ApiValues.GET_QUESTION_LIST)
public class QuestionListServlet {

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
		if (request.getRemoteUser() != null) {
			AllowCORS.addCORSHeaders(request, response, "GET");

			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String myURL = request.getRequestURL().toString();

			String rootUrl = myURL.substring(0,
					myURL.lastIndexOf(ApiValues.GET_QUESTION_LIST));

			QuestionList questionList = questionService.getAllQuestions();

			response.getWriter().println(gson.toJson(questionList));
			response.setContentType("application/json");

		} else {
			response.sendError(403,
					"User must be authenticated for this service");
		}

	}

}
