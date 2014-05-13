package com.wombling.mobilequiz.persistance;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoredQuestionRepository extends CrudRepository<StoredQuestion, String> {
	
	public List<StoredQuestion> findAll();
	
	public List<StoredQuestion> findByCurrentTrue();
}
