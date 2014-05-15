package com.wombling.mobilequiz.persistance;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.wombling.mobilequiz.api.ApiValues;
import com.wombling.mobilequiz.pojo.Question;
import com.wombling.mobilequiz.pojo.QuestionList;
import com.wombling.mobilequiz.pojo.QuestionResponse;
import com.wombling.mobilequiz.pojo.QuestionWithResults;

@Service
@Primary
public class StoredQuestionServiceImpl implements StoredQuestionService {

	@Autowired
	StoredQuestionDAO storedDAO;

	@Autowired
	ResponsesDAO responsesDAO;

	@Override
	public Question getQuestionFromId(String id) {

		Question question;

		StoredQuestion sq = storedDAO.findById(id);

		question = convertToQuestion(sq);
		return question;
	}

	@Override
	public synchronized Question getCurrentQuestion() {

		Question question = null;
		List<StoredQuestion> currentQuestions = storedDAO.findByCurrentTrue();

		if (!currentQuestions.isEmpty()) {

			StoredQuestion firstFound = currentQuestions.get(0);

			question = convertToQuestion(firstFound);

			if (question.getSecondsRemaining() == 0) {
				// this question no longer current
				firstFound.setCurrent(false);
				storedDAO.save(firstFound);
				question = null;
			}
		}
		return question;
	}

	@Override
	public synchronized Question saveQuestion(Question question) {
		StoredQuestion sq = new StoredQuestion();
		sq.setCurrent(true);
		sq.setId(question.getId());
		sq.setText(question.getQuestionText());
		Date now = new Date();
		sq.setTimeCreated(now);
		long expires = now.getTime() + (1000 * question.getSecondsRemaining());
		sq.setExpires(new Date(expires));

		// make sure nothing else is "current"
		List<StoredQuestion> currentQuestions = storedDAO.findByCurrentTrue();
		Iterator<StoredQuestion> currentIter = currentQuestions.iterator();

		while (currentIter.hasNext()) {
			StoredQuestion currentQuestion = currentIter.next();

			currentQuestion.setCurrent(false);
			storedDAO.save(currentQuestion);
		}

		StoredQuestion savedstQuestion = storedDAO.save(sq);
		Question savedQuestion = convertToQuestion(savedstQuestion);
		return savedQuestion;
	}

	@Override
	public void addVoteToQuestion(QuestionResponse questionResponse,
			String userId) {

		StoredQuestion sq = storedDAO
				.findById(questionResponse.getQuestionId());

		if (sq != null) {
			if (questionResponse.getResponse().equals(ApiValues.YES_RESPONSE)) {
				sq.setYesVotes(sq.getYesVotes() + 1);
				storedDAO.save(sq);
				Responses response = new Responses();
				response.setId(sq.getId());
				response.setUserId(userId);
				response.setResponse(questionResponse.getResponse());
				responsesDAO.save(response);
			}

			if (questionResponse.getResponse().equals(ApiValues.NO_RESPONSE)) {
				sq.setNoVotes(sq.getNoVotes() + 1);
				storedDAO.save(sq);
				Responses response = new Responses();
				response.setId(sq.getId());
				response.setUserId(userId);
				response.setResponse(questionResponse.getResponse());
				responsesDAO.save(response);
			}
		}
	}

	private Question convertToQuestion(StoredQuestion sq) {
		Question question = new Question();
		question.setId(sq.getId());
		question.setQuestionText(sq.getText());
		Date now = new Date();
		int secondsRemaining = Math.round((sq.getExpires().getTime() - now
				.getTime()) / 1000);
		if (secondsRemaining < 0) {
			secondsRemaining = 0;
		}
		question.setSecondsRemaining(secondsRemaining);

		return question;
	}

	@Override
	public QuestionList getAllQuestions() {

		QuestionList list = new QuestionList();

		List<QuestionWithResults> questions = new ArrayList<QuestionWithResults>();
		list.setQuestionList(questions);

		List<StoredQuestion> allStoredQuestions = storedDAO.findAll();

		Iterator<StoredQuestion> allIter = allStoredQuestions.iterator();

		while (allIter.hasNext()) {
			StoredQuestion sq = allIter.next();
			QuestionWithResults results = new QuestionWithResults();
			results.setCurrentQuestion(sq.isCurrent());
			results.setNoVotes(sq.getNoVotes());
			results.setQuestionText(sq.getText());
			results.setYesVotes(sq.getYesVotes());
			results.setDateTimeCreated(sq.getTimeCreated());
			results.setId(sq.getId());
			Question question = convertToQuestion(sq);
			results.setSecondsRemaining(question.getSecondsRemaining());
			questions.add(results);
		}
		return list;
	}

	@Override
	public boolean hasUserSubmittedForQuestion(String questionId, String userId) {

		List<Responses> response = responsesDAO.findByIdAndUserId(questionId,
				userId);
		boolean alreadySubmmited = (response.size() != 0);
		return alreadySubmmited;
	}

	@Override
	public List<Responses> getAllResponses() {
		return responsesDAO.findAll();
	}

	@Override
	public QuestionWithResults getQuestionWithResultsFromId(String id) {
		QuestionWithResults results = new QuestionWithResults();

		StoredQuestion sq = storedDAO.findById(id);
		if (sq != null) {
			results.setCurrentQuestion(sq.isCurrent());
			results.setNoVotes(sq.getNoVotes());
			results.setQuestionText(sq.getText());
			results.setYesVotes(sq.getYesVotes());
			results.setDateTimeCreated(sq.getTimeCreated());
			results.setId(sq.getId());
			Question question = convertToQuestion(sq);
			results.setSecondsRemaining(question.getSecondsRemaining());
		}
		return results;

	}

	@Override
	public void deleteQuestionById(String id) {
		storedDAO.delete(id);
	}

}
