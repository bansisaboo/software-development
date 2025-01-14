package com.bsaboo.service;

import org.springframework.stereotype.Service;

import com.bsaboo.domain.VerficationType;
import com.bsaboo.model.User;

public interface UserService {
	public User findUserProfileByJwt(String jwt) throws Exception;
	public User findUserByEmail(String email) throws Exception;
	public User findUserById(Long userId) throws Exception;
	public User enableTwoFA(VerficationType verficationType,String sendTo, User user);
	User updatePassword(User user,String newPassword);
}
