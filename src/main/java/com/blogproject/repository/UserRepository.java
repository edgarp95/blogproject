package com.blogproject.repository;

import org.springframework.data.repository.CrudRepository;
import com.blogproject.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
	User findByUsername(String username);

	User findById(Long id);
}
