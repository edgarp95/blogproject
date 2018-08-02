package com.blogproject.repository;

import org.springframework.data.repository.CrudRepository;
import com.blogproject.entities.Rating;

public interface RatingRepository extends CrudRepository<Rating, Long> {
	Rating findByUserNameAndPostId(String username, Long postId);

}
