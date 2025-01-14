package com.bsaboo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bsaboo.model.TwoFactorOTP;
import com.bsaboo.service.TwoFatorOtpService;

@Repository
public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String> {
	TwoFactorOTP findByUserId(Long id);

}
