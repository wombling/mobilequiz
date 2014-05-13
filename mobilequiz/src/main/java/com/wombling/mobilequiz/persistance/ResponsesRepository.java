package com.wombling.mobilequiz.persistance;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsesRepository extends CrudRepository<Responses, String> {

	public List<Responses> findAll();

	public List<Responses> findByIdAndUserId(String id, String userId);
}
