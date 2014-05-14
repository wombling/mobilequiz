package com.wombling.mobilequiz.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import javax.servlet.ServletException;
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
import com.wombling.mobilequiz.pojo.InitialQuestion;
import com.wombling.mobilequiz.pojo.Question;
import com.wombling.mobilequiz.user.AllowCORS;
import com.wombling.mobilequiz.user.QuestionService;

@Controller
@RequestMapping(ApiValues.CREATE_QUESTION)
public class CreateQuestionServlet {

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

		if (request.getRemoteUser() != null) {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String myURL = request.getRequestURL().toString();

			String rootUrl = myURL.substring(0,
					myURL.lastIndexOf(ApiValues.CREATE_QUESTION) - 2);
			InitialQuestion question = null;
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(request.getInputStream()));

				question = gson.fromJson(reader, InitialQuestion.class);

				// create a new question from this

				String guid = UUID.randomUUID().toString();

				Question newQuestion = new Question();

				newQuestion.setId(guid);
				newQuestion.setQuestionText(question.getQuestionText());
				newQuestion.setSecondsRemaining(question
						.getSecondsUntilExpiry());

				questionService.createNewQuestion(newQuestion, rootUrl);

				response.sendRedirect(rootUrl + "/b" + ApiValues.GET_QUESTION);

			}

			catch (JsonSyntaxException e) {
				response.sendError(400, "unrecognised JSON");
			}
		} else {
			response.sendError(403,
					"User must be authenticated for this service");
		}

	}
}
