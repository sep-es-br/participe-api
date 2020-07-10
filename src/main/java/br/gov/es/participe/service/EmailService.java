package br.gov.es.participe.service;

import java.io.IOException;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class EmailService {

	@Autowired 
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
    private String from;
	
	@Autowired
    private Configuration freemarkerConfig;
	 
	
	/*public void sendEmail(String to, String title, String text) {// HashMap<String, String> data) {
		SimpleMailMessage message = new SimpleMailMessage();
		
		//String body = generateBody(data);
		message.setText(text);
        message.setTo(to);
        message.setSubject(title);
        
        mailSender.send(message);
	}*/
	
	public void sendEmail(String to, String title, HashMap<String, String> data) {
		MimeMessage message = mailSender.createMimeMessage();
		 
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		String body = null;

		
		try {
			body = generateBody(data);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setText(body, true);
	        helper.setSubject(title);
		} catch (MessagingException | IOException | TemplateException e) {
			e.printStackTrace();
		}

		mailSender.send(message);
	}
	
	private String generateBody(HashMap<String, String> data) throws IOException, TemplateException {
		freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/static");
		Template t = freemarkerConfig.getTemplate("emailBody.ftl");
        String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, data);
		return text;
	}
}
