package com.bsaboo.response;

import lombok.Data;

@Data
public class AuthResponse {
	private String jwt;
	private boolean status;
	private String message;
	private boolean isTwoFA;
	private String session;
	
}
