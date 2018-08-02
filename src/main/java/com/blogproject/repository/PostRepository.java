package com.blogproject.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.blogproject.entities.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
	List<Post> findAllByOrderByTitleAsc();

	List<Post> findAllByOrderByDateDesc();

	List<Post> findAllByOrderByLastUpdateTimeDesc();

	Post findById(Long id);

	Page<Post> findAllByOrderByLastUpdateTimeDesc(Pageable pageable);

	List<Post> findByUserNameOrderByLastUpdateTimeDesc(String username);

	@Query(value = "SELECT * FROM post t WHERE "
			+ "LOWER(t.title) LIKE LOWER(CONCAT('%',:searchTerm, '%')) ORDER BY(t.title)", nativeQuery = true)
	List<Post> findBytitle(@Param("searchTerm") String title);

}
