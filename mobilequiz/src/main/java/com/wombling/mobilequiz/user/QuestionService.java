package com.wombling.mobilequiz.user;

import java.util.List;

import com.wombling.mobilequiz.exceptions.AlreadyVotedException;
import com.wombling.mobilequiz.exceptions.NoCurrentQuestionException;
import com.wombling.mobilequiz.exceptions.QuestionNotFoundException;
import com.wombling.mobilequiz.exceptions.QuestionNotValidException;
import com.wombling.mobilequiz.persistance.Responses;
import com.wombling.mobilequiz.pojo.Question;
import com.wombling.mobilequiz.pojo.QuestionList;
import com.wombling.mobilequiz.pojo.QuestionResponse;
import com.wombling.mobilequiz.pojo.QuestionWithResults;

public interface QuestionService {

	public Question getCurrentQuestion(String rootUrl)
			throws NoCurrentQuestionException;

	public void registerQuestionResponse(QuestionResponse questionResponse,
			String userId) throws QuestionNotValidException, AlreadyVotedException;
	
	public Question createNewQuestion(Question question, String rootUrl);
	
	public QuestionList getAllQuestions(String rootUrl);
	
	public List<Responses> getAllResponses();
	
	public QuestionWithResults getQuestionById(String id, String rootUrl) throws QuestionNotFoundException;

	public void deleteQuestion(QuestionWithResults question) throws QuestionNotFoundException;

}
