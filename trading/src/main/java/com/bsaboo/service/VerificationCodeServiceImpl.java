package com.bsaboo.service;

import java.util.Optional;

import org.apache.catalina.startup.ListenerCreateRule.OptionalListener;
import org.springframework.stereotype.Service;

import com.bsaboo.domain.VerficationType;
import com.bsaboo.model.User;
import com.bsaboo.model.VerificationCode;
import com.bsaboo.repository.VerificationCodeRepository;
import com.bsaboo.utils.OtpUtils;


@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
	
	private VerificationCodeRepository verificationCodeRepository;

	@Override
	public VerificationCode sendVerificationCode(User user, VerficationType verificationType) {
		VerificationCode code = new VerificationCode();
		code.setOtp(OtpUtils.generateOTP());
		code.setVerficationType(verificationType);
		code.setUser(user);
		return verificationCodeRepository.save(code);
	}

	@Override
	public VerificationCode getVerificationCodeById(Long id) throws Exception {
		Optional<VerificationCode> code = verificationCodeRepository.findById(id);
		if(code.isPresent()) {
			return code.get();
		}
		throw new Exception("Code not found");
	}

	@Override
	public VerificationCode getVerificationCodeByUser(Long userId) {
		
		return verificationCodeRepository.findByUserId(userId);
	}

	@Override
	public void deleteVerificationCodeById(VerificationCode verificationCode) {
		verificationCodeRepository.delete(verificationCode);
		
	}

}
