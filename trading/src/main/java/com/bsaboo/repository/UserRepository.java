package com.bsaboo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsaboo.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	User findByEmail(String email);

}
