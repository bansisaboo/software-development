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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bsaboo.config.JwtProvider;
import com.bsaboo.model.User;
import com.bsaboo.repository.UserRepository;
import com.bsaboo.response.AuthResponse;
import com.bsaboo.service.CustomUserDetailsService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception{
		
		User isEmailExist = userRepository.findByEmail(user.getEmail());

		if(isEmailExist !=null) {
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
	public ResponseEntity<AuthResponse> signIn(@RequestBody User user) throws Exception{
		
		
		Authentication auth = authenticate(user.getEmail(),user.getPassword());

		SecurityContextHolder.getContext().setAuthentication(auth);
		
		String jwt = JwtProvider.generateToken(auth);
		
		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(jwt);
		authResponse.setStatus(true);
		authResponse.setMessage("Logged In successfully");
		
		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	} 
	
	private Authentication authenticate(String userName, String password) {
		
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
		
		if(userDetails == null) {
			throw new BadCredentialsException("Invalid Username");
		}
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid Password");

		}
		
		return new UsernamePasswordAuthenticationToken(userDetails,password, userDetails.getAuthorities());
		
	}
	
	
}
