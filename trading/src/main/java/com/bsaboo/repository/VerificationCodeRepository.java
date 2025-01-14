package com.bsaboo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsaboo.model.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long>{
	
	public VerificationCode findByUserId(Long userId);

}
