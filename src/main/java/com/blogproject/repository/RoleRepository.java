package com.blogproject.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.blogproject.auth.Role;



public interface RoleRepository extends CrudRepository<Role, Long>{
	List<Role> findAll();
}