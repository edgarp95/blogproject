package com.blogproject.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.blogproject.post.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
	List<Post> findAllByOrderByTitleAsc();
	List<Post> findAllByOrderByDateDesc();
	List<Post> findAllByOrderByLastUpdateTimeDesc();
	Post findById(Long id);
}
