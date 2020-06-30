package br.gov.es.participe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired 
	private JavaMailSender mailSender;
	
	public void sendEmail(String to, String title, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setText(text);
        message.setTo(to);
        message.setSubject(title);
        
        mailSender.send(message);
	}
}
