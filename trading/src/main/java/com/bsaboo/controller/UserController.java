package com.bsaboo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.bsaboo.domain.VerficationType;
import com.bsaboo.model.User;
import com.bsaboo.model.VerificationCode;
import com.bsaboo.service.EmailService;
import com.bsaboo.service.UserService;
import com.bsaboo.service.VerificationCodeService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private VerificationCodeService verificationCodeService;
	
	
	@GetMapping("/api/users/profile")
	public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception{
		
		User user = userService.findUserProfileByJwt(jwt);
		return new ResponseEntity<User>(user,HttpStatus.OK);
		
	}
	@PostMapping("/api/users/verifciation/{verificationType}/send-otp")
	public ResponseEntity<String> sendVerifciationOtp(@RequestHeader("Authorization") String jwt,
			@PathVariable VerficationType verificationType) throws Exception{
		
		User user = userService.findUserProfileByJwt(jwt);
		
		VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
		if(verificationCode==null) {
			verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
		}
		
		if(verificationType.equals(VerficationType.EMAIL)){
			emailService.sendVerificationEmail(user.getEmail(), verificationCode.getOtp());
		}
		
		return new ResponseEntity<>("Otp sent successfully",HttpStatus.OK);
		
	}
	
	@PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
	public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt,
			@PathVariable String otp) throws Exception{
		
		User user = userService.findUserProfileByJwt(jwt);
		
		VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
		
		String sendTo =  verificationCode.getVerficationType().equals(VerficationType.EMAIL) ? verificationCode.getEmail() :verificationCode.getMobile();

		boolean isVerified = verificationCode.getOtp().equals(otp);
		
		if(isVerified) {
			User updateduser = userService.enableTwoFA(verificationCode.getVerficationType(),sendTo, user);
			verificationCodeService.deleteVerificationCodeById(verificationCode);
			return new ResponseEntity<>(user,HttpStatus.OK);
		}
		throw new Exception("Incorrect OTP");
		
	}
}
