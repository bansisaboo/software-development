package com.bsaboo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsaboo.domain.VerficationType;
import com.bsaboo.model.ForgotPasswordToken;
import com.bsaboo.model.User;
import com.bsaboo.repository.ForgotPasswordRepository;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService{

	
	@Autowired
	private ForgotPasswordRepository forgotPasswordRepository;
	
	@Override
	public ForgotPasswordToken createToken(User user, String id, String otp, VerficationType verificationType,
			String sendTo) {
		ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
		forgotPasswordToken.setUser(user);
		forgotPasswordToken.setSendTo(sendTo);
		forgotPasswordToken.setVerificationType(verificationType);
		forgotPasswordToken.setOtp(otp);
		forgotPasswordToken.setId(id);
	
		return forgotPasswordRepository.save(forgotPasswordToken);
	}

	@Override
	public ForgotPasswordToken findById(String id) {
		Optional<ForgotPasswordToken> token = forgotPasswordRepository.findById(id);
		return token.orElse(null);
	}

	@Override
	public ForgotPasswordToken findByUser(Long userId) {
		
		return forgotPasswordRepository.findByUserId(userId);
	}

	@Override
	public void deleteToken(ForgotPasswordToken token) {
		
		forgotPasswordRepository.delete(token);
	}

}
