package com.bsaboo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsaboo.config.JwtProvider;
import com.bsaboo.model.TwoFactorOTP;
import com.bsaboo.model.User;
import com.bsaboo.repository.UserRepository;
import com.bsaboo.response.AuthResponse;
import com.bsaboo.service.CustomUserDetailsService;
import com.bsaboo.service.EmailService;
import com.bsaboo.service.TwoFatorOtpService;
import com.bsaboo.utils.OtpUtils;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private TwoFatorOtpService twoFatorOtpService;

	@Autowired
	private EmailService emailService;

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

		User isEmailExist = userRepository.findByEmail(user.getEmail());

		if (isEmailExist != null) {
			throw new Exception("Email already exist");
		}
		User newUser = new User();
		newUser.setFullName(user.getFullName());
		newUser.setEmail(user.getEmail());
		newUser.setPassword(passwordEncoder.encode(user.getPassword()));

		User savedUser = userRepository.save(newUser);

		Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

		SecurityContextHolder.getContext().setAuthentication(auth);

		String jwt = JwtProvider.generateToken(auth);

		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(jwt);
		authResponse.setStatus(true);
		authResponse.setMessage("Registered successfully");

		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signIn(@RequestBody User user) throws Exception {

		Authentication auth = authenticate(user.getEmail(), user.getPassword());

		SecurityContextHolder.getContext().setAuthentication(auth);

		String jwt = JwtProvider.generateToken(auth);

		User authUser = userRepository.findByEmail(user.getEmail());

		if (user.getTwoFA().isEnabled()) {
			AuthResponse authResponse = new AuthResponse();
			authResponse.setMessage("Two Factor authentication is enabled");
			authResponse.setTwoFA(true);
			String otp = OtpUtils.generateOTP();

			TwoFactorOTP oldTwoFactorOTP = twoFatorOtpService.findByUser(authUser.getId());
			if (oldTwoFactorOTP != null) {
				twoFatorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
			}
			TwoFactorOTP newTwoFactorOTP = twoFatorOtpService.createTwoFatorOtp(authUser, otp, jwt);

			emailService.sendVerificationEmail(authUser.getEmail(), otp);

			authResponse.setSession(newTwoFactorOTP.getId());
			return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
		}

		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(jwt);
		authResponse.setStatus(true);
		authResponse.setMessage("Logged In successfully");

		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}

	private Authentication authenticate(String userName, String password) {

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

		if (userDetails == null) {
			throw new BadCredentialsException("Invalid Username");
		}
		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid Password");

		}

		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

	}
	
	@PostMapping("/two-factor/otp/{otp}")
	public ResponseEntity<AuthResponse> verifyOtp(@PathVariable String otp,@RequestParam String id) throws Exception{
		
		TwoFactorOTP twoFactorOTP = twoFatorOtpService.findById(id);
		if(twoFatorOtpService.verifyTwoFactorOtp(twoFactorOTP,otp)) {
			AuthResponse response = new AuthResponse();
			response.setMessage("Otp verfiied successfully");
			response.setTwoFA(true);
			response.setJwt(twoFactorOTP.getJwt());
			return new ResponseEntity<>(response,HttpStatus.OK);
		}
		
		throw new Exception("Invlaid Otp");
	}

}
