package com.wombling.mobilequiz.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.wombling.mobilequiz.api.ApiValues;
import com.wombling.mobilequiz.exceptions.AlreadyVotedException;
import com.wombling.mobilequiz.exceptions.NoCurrentQuestionException;
import com.wombling.mobilequiz.exceptions.QuestionNotValidException;
import com.wombling.mobilequiz.persistance.Responses;
import com.wombling.mobilequiz.persistance.StoredQuestionService;
import com.wombling.mobilequiz.pojo.Question;
import com.wombling.mobilequiz.pojo.QuestionList;
import com.wombling.mobilequiz.pojo.QuestionResponse;

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
	public QuestionList getAllQuestions() {

		QuestionList list = sqService.getAllQuestions();

		return list;
	}

	@Override
	public List<Responses> getAllResponses() {
		return sqService.getAllResponses();
	}

}
