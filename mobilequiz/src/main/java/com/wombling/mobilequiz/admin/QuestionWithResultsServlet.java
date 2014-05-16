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
import com.wombling.mobilequiz.exceptions.QuestionNotFoundException;
import com.wombling.mobilequiz.pojo.QuestionWithResults;
import com.wombling.mobilequiz.user.AllowCORS;
import com.wombling.mobilequiz.user.QuestionService;

@Controller
@RequestMapping(ApiValues.GET_QUESTION_DETAILS + "/**")
public class QuestionWithResultsServlet {

	@Autowired
	QuestionService questionService;

	@RequestMapping(method = RequestMethod.OPTIONS)
	public void doOptions(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		AllowCORS.addCORSHeaders(request, response, "GET, DELETE");
	}

	@RequestMapping(method = RequestMethod.GET)
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (request.getRemoteUser() != null) {
			AllowCORS.addCORSHeaders(request, response, "GET, DELETE");

			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String myURL = request.getRequestURL().toString();

			String rootUrl = myURL.substring(0,
					myURL.lastIndexOf(ApiValues.GET_QUESTION_DETAILS));

			String id;
			try {
				id = getQuestionId(request);

				QuestionWithResults question = questionService.getQuestionById(
						id, rootUrl);

				response.getWriter().println(gson.toJson(question));
				response.setContentType("application/json");

			} catch (QuestionNotFoundException e) {
				response.sendError(404, "Question with that id not found");
			}
		} else {
			response.sendError(403,
					"User must be authenticated for this service");
		}

	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getRemoteUser() != null) {
			AllowCORS.addCORSHeaders(request, response, "GET, DELETE");
			String myURL = request.getRequestURL().toString();

			String rootUrl = myURL.substring(0,
					myURL.lastIndexOf(ApiValues.GET_QUESTION_DETAILS));

			String id;
			try {
				id = getQuestionId(request);

				QuestionWithResults question = questionService.getQuestionById(
						id, rootUrl);

				questionService.deleteQuestion(question);

				response.getWriter().println("{\"deleted\":true}");
				response.setContentType("application/json");

			} catch (QuestionNotFoundException e) {
				response.sendError(404, "Question with that id not found");
			}
		} else {
			response.sendError(403,
					"User must be authenticated for this service");
		}

	}

	private String getQuestionId(HttpServletRequest request)
			throws QuestionNotFoundException {
		String myURI = request.getRequestURI();
		String id = null;
		try {
			id = myURI.substring(myURI.indexOf(ApiValues.GET_QUESTION_DETAILS)
					+ ApiValues.GET_QUESTION_DETAILS.length() + 1);
		} catch (Exception e) {

			throw new QuestionNotFoundException();
		}

		return id;
	}

}
