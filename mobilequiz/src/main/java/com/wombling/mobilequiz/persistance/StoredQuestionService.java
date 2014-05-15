package com.wombling.mobilequiz.persistance;

import java.util.List;

import com.wombling.mobilequiz.pojo.Question;
import com.wombling.mobilequiz.pojo.QuestionList;
import com.wombling.mobilequiz.pojo.QuestionResponse;
import com.wombling.mobilequiz.pojo.QuestionWithResults;

public interface StoredQuestionService {

	public Question getQuestionFromId(String id);

	public Question getCurrentQuestion();

	public Question saveQuestion(Question question);

	public void addVoteToQuestion(QuestionResponse questionResponse, String userId);

	public QuestionList getAllQuestions();
	
	public boolean hasUserSubmittedForQuestion(String questionId, String userId);
	
	public List<Responses> getAllResponses();
	
	public QuestionWithResults getQuestionWithResultsFromId(String id);

	public void deleteQuestionById(String id);
}
