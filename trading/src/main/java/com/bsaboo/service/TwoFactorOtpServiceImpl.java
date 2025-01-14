package com.bsaboo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsaboo.model.TwoFactorOTP;
import com.bsaboo.model.User;
import com.bsaboo.repository.TwoFactorOtpRepository;

@Service
public class TwoFactorOtpServiceImpl implements TwoFatorOtpService {

	@Autowired
	private TwoFactorOtpRepository twoFactorOtpRepository;

	@Override
	public TwoFactorOTP createTwoFatorOtp(User user, String otp, String jwt) {
		UUID uuid = UUID.randomUUID();

		String id = uuid.toString();
		
		TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
		twoFactorOTP.setId(id);
		twoFactorOTP.setJwt(jwt);
		twoFactorOTP.setOtp(otp);
		twoFactorOTP.setUser(user);
		
		return twoFactorOtpRepository.save(twoFactorOTP);
		
	}

	@Override
	public TwoFactorOTP findByUser(Long userId) {
		return twoFactorOtpRepository.findByUserId(userId);
	}

	@Override
	public TwoFactorOTP findById(String id) {
		Optional<TwoFactorOTP> optional = twoFactorOtpRepository.findById(id);
		return optional.orElse(null);
	}

	@Override
	public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp) {
		return twoFactorOTP.getOtp().equals(otp);
	}

	@Override
	public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP) {
		twoFactorOtpRepository.delete(twoFactorOTP);
	}

}
