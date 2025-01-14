package com.bsaboo.service;

import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	
	
	private JavaMailSender javaMailSender;
	
	public void sendVerificationEmail(String email,String otp) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,"utf-8");
		
		String subject ="OTP Requested";
		String text = "Your verficaiion code is " +otp;
		
		mimeMessageHelper.setSubject(subject);
		mimeMessageHelper.setText(text);
		mimeMessageHelper.setTo(email);
		
		try {
			javaMailSender.send(message);
		}catch (Exception e) {
			throw new MailSendException(e.getMessage());
		}
		
	}
}
