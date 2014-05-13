package com.wombling.mobilequiz.persistance;

import java.util.List;

public interface ResponsesDAO {

	public Responses save(Responses entity);

	public Responses findById(String id);

	public void delete(String id);

	public void delete(Responses entity);

	public void deleteAll();

	public List<Responses> findAll();

	public List<Responses> findByIdAndUserId(String id, String userId);

}
