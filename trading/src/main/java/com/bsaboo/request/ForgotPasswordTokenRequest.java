package com.bsaboo.request;

import com.bsaboo.domain.VerficationType;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class ForgotPasswordTokenRequest {
	
	private String sendTo;
	private VerficationType verficationType;

}
