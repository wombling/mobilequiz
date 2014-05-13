package com.wombling.mobilequiz.persistance;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class ResponsesImpl implements ResponsesDAO {

	@Resource
	ResponsesRepository repository;

	@Override
	public Responses save(Responses entity) {
		return repository.save(entity);
	}

	@Override
	public Responses findById(String id) {
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
	public void delete(Responses entity) {
		repository.delete(entity);

	}

	@Override
	public List<Responses> findAll() {
		return repository.findAll();
	}

	@Override
	public List<Responses> findByIdAndUserId(String id, String userId) {
		return repository.findByIdAndUserId(id, userId);
	}
}
