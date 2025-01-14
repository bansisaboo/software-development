package com.bsaboo.service;


import com.bsaboo.domain.VerficationType;
import com.bsaboo.model.User;
import com.bsaboo.model.VerificationCode;


public interface VerificationCodeService {
	
	VerificationCode sendVerificationCode(User user,VerficationType verficationType);
	VerificationCode getVerificationCodeById(Long id) throws Exception;
	VerificationCode getVerificationCodeByUser(Long userId);

	void deleteVerificationCodeById(VerificationCode verificationCode);
	

}
