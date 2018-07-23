package com.blogproject.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.blogproject.comment.Comment;



public interface CommentRepository extends CrudRepository<Comment, Long> {
	List<Comment> findByPostIdOrderByLastUpdateTimeDesc(Long postid);
	Comment findByid(Long id);
}
