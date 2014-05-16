package com.wombling.mobilequiz.user;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.wombling.mobilequiz.admin.QuestionsWebSocket;
import com.wombling.mobilequiz.api.ApiValues;
import com.wombling.mobilequiz.exceptions.AlreadyVotedException;
import com.wombling.mobilequiz.exceptions.NoCurrentQuestionException;
import com.wombling.mobilequiz.exceptions.QuestionNotFoundException;
import com.wombling.mobilequiz.exceptions.QuestionNotValidException;
import com.wombling.mobilequiz.persistance.Responses;
import com.wombling.mobilequiz.persistance.StoredQuestionService;
import com.wombling.mobilequiz.pojo.Question;
import com.wombling.mobilequiz.pojo.QuestionList;
import com.wombling.mobilequiz.pojo.QuestionResponse;
import com.wombling.mobilequiz.pojo.QuestionWithResults;

@Service
@Primary
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	StoredQuestionService sqService;

	public Question getCurrentQuestion(String rootUrl)
			throws NoCurrentQuestionException {

		Question question = sqService.getCurrentQuestion();

		if (question == null) {
			throw new NoCurrentQuestionException();
		}

		question.setResponseUrl(rootUrl + ApiValues.RESPONSE + "/"
				+ question.getId());
		return question;

	}

	public void registerQuestionResponse(QuestionResponse questionResponse,
			String userId) throws QuestionNotValidException,
			AlreadyVotedException {

		Question question = sqService.getQuestionFromId(questionResponse
				.getQuestionId());

		if (question != null) {
			// check that response is valid
			if (questionResponse.getResponse().equals(ApiValues.NO_RESPONSE)
					|| questionResponse.getResponse().equals(
							ApiValues.YES_RESPONSE)) {
				// check user hasn't already voted
				if (!sqService.hasUserSubmittedForQuestion(question.getId(),
						userId)) {
					sqService.addVoteToQuestion(questionResponse, userId);

					// inform everyone that question has updated
					QuestionsWebSocket.sendUpdate(this);
				} else {
					throw new AlreadyVotedException();
				}
			} else {
				throw new QuestionNotValidException();
			}
		} else {
			throw new QuestionNotValidException();
		}
	}

	@Override
	public Question createNewQuestion(Question question, String rootUrl) {

		Question newQuestion = sqService.saveQuestion(question);

		newQuestion.setResponseUrl(rootUrl + ApiValues.RESPONSE + "/"
				+ newQuestion.getId());
		return newQuestion;
	}

	@Override
	public QuestionList getAllQuestions(String rootUrl) {

		QuestionList list = sqService.getAllQuestions();

		Iterator<QuestionWithResults> listIter = list.getQuestionList()
				.iterator();

		while (listIter.hasNext()) {
			QuestionWithResults question = listIter.next();

			question.setResourceLink(rootUrl + ApiValues.GET_QUESTION_DETAILS
					+ "/" + question.getId());
		}

		return list;
	}

	@Override
	public List<Responses> getAllResponses() {
		return sqService.getAllResponses();
	}

	@Override
	public QuestionWithResults getQuestionById(String id, String rootUrl)
			throws QuestionNotFoundException {

		QuestionWithResults question = sqService
				.getQuestionWithResultsFromId(id);

		if (question == null) {
			throw new QuestionNotFoundException();
		}

		return question;
	}

	@Override
	public void deleteQuestion(QuestionWithResults question)
			throws QuestionNotFoundException {

		sqService.deleteQuestionById(question.getId());

	}

}
