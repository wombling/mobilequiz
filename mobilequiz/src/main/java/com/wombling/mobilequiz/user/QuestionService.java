package com.wombling.mobilequiz.user;

import java.util.List;

import com.wombling.mobilequiz.exceptions.AlreadyVotedException;
import com.wombling.mobilequiz.exceptions.NoCurrentQuestionException;
import com.wombling.mobilequiz.exceptions.QuestionNotValidException;
import com.wombling.mobilequiz.persistance.Responses;
import com.wombling.mobilequiz.pojo.Question;
import com.wombling.mobilequiz.pojo.QuestionList;
import com.wombling.mobilequiz.pojo.QuestionResponse;

public interface QuestionService {

	public Question getCurrentQuestion(String rootUrl)
			throws NoCurrentQuestionException;

	public void registerQuestionResponse(QuestionResponse questionResponse,
			String userId) throws QuestionNotValidException, AlreadyVotedException;
	
	public Question createNewQuestion(Question question, String rootUrl);
	
	public QuestionList getAllQuestions();
	
	public List<Responses> getAllResponses();

}
