package com.bsaboo.service;

import com.bsaboo.domain.VerficationType;
import com.bsaboo.model.ForgotPasswordToken;
import com.bsaboo.model.User;

public interface ForgotPasswordService {
	
	ForgotPasswordToken createToken(User user, String id, String otp, VerficationType verificationType, String sendTo);
	ForgotPasswordToken findById(String id);
	ForgotPasswordToken findByUser(Long userId);
	void deleteToken(ForgotPasswordToken token);

}
