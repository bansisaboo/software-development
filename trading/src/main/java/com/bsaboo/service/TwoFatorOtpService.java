package com.bsaboo.service;


import com.bsaboo.model.TwoFactorOTP;
import com.bsaboo.model.User;

public interface TwoFatorOtpService {
	
	TwoFactorOTP createTwoFatorOtp(User user,String otp,String jwt) ;
	TwoFactorOTP findByUser(Long userId);
	TwoFactorOTP findById(String id);
	boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP,String otp);
	void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP);

}
