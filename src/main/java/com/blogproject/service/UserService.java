package com.blogproject.service;

import com.blogproject.entities.User;

public interface UserService {
	void save(User user);

	User findByUsername(String username);

	User findById(Long id);

	void addDetails(User user);
}
