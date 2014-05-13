package com.wombling.mobilequiz.persistance;

import java.util.List;

public interface StoredQuestionDAO {

	public StoredQuestion save(StoredQuestion entity);

	public StoredQuestion findById(String id);

	public void delete(String id);

	public void delete(StoredQuestion entity);

	public void deleteAll();

	public List<StoredQuestion> findAll();

	public List<StoredQuestion> findByCurrentTrue();

}
