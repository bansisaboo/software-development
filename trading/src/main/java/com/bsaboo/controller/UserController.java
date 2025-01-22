package com.bsaboo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsaboo.domain.VerficationType;
import com.bsaboo.model.ForgotPasswordToken;
import com.bsaboo.model.User;
import com.bsaboo.model.VerificationCode;
import com.bsaboo.request.ForgotPasswordTokenRequest;
import com.bsaboo.request.ResetPasswordRequest;
import com.bsaboo.response.ApiResponse;
import com.bsaboo.response.AuthResponse;
import com.bsaboo.service.EmailService;
import com.bsaboo.service.ForgotPasswordService;
import com.bsaboo.service.UserService;
import com.bsaboo.service.VerificationCodeService;
import com.bsaboo.utils.OtpUtils;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private VerificationCodeService verificationCodeService;

	@Autowired
	private ForgotPasswordService forgotPasswordService;

	@GetMapping("/api/users/profile")
	public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {

		User user = userService.findUserProfileByJwt(jwt);
		return new ResponseEntity<User>(user, HttpStatus.OK);

	}

	@PostMapping("/api/users/verification/{verificationType}/send-otp")
	public ResponseEntity<String> sendVerifciationOtp(@RequestHeader("Authorization") String jwt,
			@PathVariable VerficationType verificationType) throws Exception {

		User user = userService.findUserProfileByJwt(jwt);

		VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
		if (verificationCode == null) {
			verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
		}

		if (verificationType.equals(VerficationType.EMAIL)) {
			emailService.sendVerificationEmail(user.getEmail(), verificationCode.getOtp());
		}

		return new ResponseEntity<>("Otp sent successfully", HttpStatus.OK);

	}

	@PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
	public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt,
			@PathVariable String otp) throws Exception {

		User user = userService.findUserProfileByJwt(jwt);

		VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

		String sendTo = verificationCode.getVerficationType().equals(VerficationType.EMAIL)
				? verificationCode.getEmail()
				: verificationCode.getMobile();

		boolean isVerified = verificationCode.getOtp().equals(otp);

		if (isVerified) {
			User updateduser = userService.enableTwoFA(verificationCode.getVerficationType(), sendTo, user);
			verificationCodeService.deleteVerificationCodeById(verificationCode);
			return new ResponseEntity<>(updateduser, HttpStatus.OK);
		}
		throw new Exception("Incorrect OTP");

	}

	@PostMapping("/api/users/reset-password/send-otp")
	public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
			@RequestBody ForgotPasswordTokenRequest forgotPasswordTokenRequest) throws Exception {

		User user = userService.findUserByEmail(forgotPasswordTokenRequest.getSendTo());
		String otp = OtpUtils.generateOTP();
		UUID uuid = UUID.randomUUID();
		String id = uuid.toString();

		ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findByUser(user.getId());
		if (forgotPasswordToken == null) {
			forgotPasswordToken = forgotPasswordService.createToken(user, id, otp,
					forgotPasswordTokenRequest.getVerficationType(), forgotPasswordTokenRequest.getSendTo());
		}

		if (forgotPasswordToken.getVerificationType().equals(VerficationType.EMAIL)) {
			emailService.sendVerificationEmail(user.getEmail(), forgotPasswordToken.getOtp());
		}

		AuthResponse response = new AuthResponse();
		response.setSession(forgotPasswordToken.getId());
		response.setMessage("Forgot Password OTP sent successfully");

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PatchMapping("/auth/users/reset-password/verify-otp")
	public ResponseEntity<ApiResponse> resetPassword(@RequestParam String id,
			@RequestBody ResetPasswordRequest resetPasswordRequest, @RequestHeader("Authorization") String jwt)
			throws Exception {

		ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

		boolean isVerified = forgotPasswordToken.getOtp().equals(resetPasswordRequest.getOtp());
		if (isVerified) {
			userService.updatePassword(forgotPasswordToken.getUser(), resetPasswordRequest.getPassword());
			ApiResponse response = new ApiResponse();
			response.setMessage("Password reset successfully");
			return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
		}
		throw new Exception("Incorrect Otp");

	}
}
