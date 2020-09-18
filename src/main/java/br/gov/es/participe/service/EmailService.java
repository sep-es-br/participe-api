package br.gov.es.participe.service;

import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
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
	
	@Autowired
    private Logger log;
	
	public void sendEmail(String to, String title, Map<String, String> data) {
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
			log.error("Error template email", e);
		}

		mailSender.send(message);
	}
	
	private String generateBody(Map<String, String> data) throws IOException, TemplateException {
		freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/static");
		Template t = freemarkerConfig.getTemplate("emailBody.ftl");
        return FreeMarkerTemplateUtils.processTemplateIntoString(t, data);
	}
}
