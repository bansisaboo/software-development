package com.bsaboo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bsaboo.config.JwtProvider;
import com.bsaboo.domain.VerficationType;
import com.bsaboo.model.TwoFA;
import com.bsaboo.model.TwoFactorOTP;
import com.bsaboo.model.User;
import com.bsaboo.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public User findUserProfileByJwt(String jwt) throws Exception {
		String email = JwtProvider.getEmailFromToken(jwt);
		User user = userRepository.findByEmail(email);
		
		if(user == null) {
			throw new Exception("User not found");
		}
		return user;
	}

	@Override
	public User findUserByEmail(String email) throws Exception {
		
		User user = userRepository.findByEmail(email);
		
		if(user == null) {
			throw new Exception("User not found");
		}
		return user;
	}

	@Override
	public User findUserById(Long userId) throws Exception {
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) {
			throw new Exception("User not found");

		}
		return user.get();
	}

	@Override
	public User enableTwoFA(VerficationType verficationType,String sendTo, User user) {
		TwoFA twoFA = new TwoFA();
		twoFA.setSendTo(verficationType);
		twoFA.setEnabled(true);
		
		user.setTwoFA(twoFA);
		return userRepository.save(user);
	}


	@Override
	public User updatePassword(User user, String newPassword) {
		user.setPassword(newPassword);
		return userRepository.save(user);
	}


}
