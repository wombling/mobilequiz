package com.wombling.mobilequiz.persistance;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class StoredQuestionImpl implements StoredQuestionDAO {

	@Resource
	StoredQuestionRepository repository;

	@Override
	public StoredQuestion save(StoredQuestion entity) {
		return repository.save(entity);
	}

	@Override
	public StoredQuestion findById(String id) {
		return repository.findOne(id);
	}

	@Override
	public void delete(String id) {
		repository.delete(id);

	}

	@Override
	public void deleteAll() {
		repository.deleteAll();

	}

	@Override
	public void delete(StoredQuestion entity) {
		repository.delete(entity);

	}

	@Override
	public List<StoredQuestion> findAll() {
		return repository.findAll();
	}

	@Override
	public List<StoredQuestion> findByCurrentTrue() {
		return repository.findByCurrentTrue();
	}
}
