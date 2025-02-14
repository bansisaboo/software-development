package com.bsaboo.model;

import com.bsaboo.domain.VerficationType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class ForgotPasswordToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String Id;
	
	
	@OneToOne
	private User user;
	
	private String otp;

	private VerficationType verificationType;
	
	private String sendTo;
}
