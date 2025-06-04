package br.gov.es.participe.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.swing.JEditorPane;
import javax.xml.bind.DatatypeConverter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
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

	public void sendEmailPreRegistration(String to, String title, Map<String, String> data, byte[] imageQR) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		String body = null;
		
		try {
			body = generateBodyPreRegistration(data);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setText(body, true);
			helper.setSubject(title);
			this.base64ToImage(imageQR);
			FileSystemResource qrCode = new FileSystemResource("qrcode.png");
			FileSystemResource poweredBy = new FileSystemResource("brasao_white.png");
			helper.addAttachment("QRCODE.png", qrCode);
			helper.addInline("poweredBy", poweredBy);
			helper.addInline("qrCode", qrCode);
		} catch (Exception e ) {
			log.error("Error template email", e);
		}

		mailSender.send(message);
	}

	public void sendEmailPreRegistration(String[] to, String title, Map<String, String> data, byte[] imageQR) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		String body = null;
		
		try {
			body = generateBodyPreRegistration(data);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setText(body, true);
			helper.setSubject(title);
			this.base64ToImage(imageQR);
			FileSystemResource qrCode = new FileSystemResource("qrcode.png");
			FileSystemResource poweredBy = new FileSystemResource("brasao_white.png");
			helper.addAttachment("QRCODE.png", qrCode);
			helper.addInline("poweredBy", poweredBy);
			helper.addInline("qrCode", qrCode);
		} catch (Exception e ) {
			log.error("Error template email", e);
		}

		mailSender.send(message);
	}
	
	private String generateBody(Map<String, String> data) throws IOException, TemplateException {
		freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/static");
		Template t = freemarkerConfig.getTemplate("emailBody.ftl");
        return FreeMarkerTemplateUtils.processTemplateIntoString(t, data);
	}

	private String generateBodyPreRegistration(Map<String, String> data) throws IOException, TemplateException {
		freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/static");
		Template t = freemarkerConfig.getTemplate("preRegistrationQrCode.ftl");
		return FreeMarkerTemplateUtils.processTemplateIntoString(t, data);
	}


	private File htmlToImage(String html) throws Exception {
        int width = 530;
        int height = 600;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        JEditorPane jep = new JEditorPane("text/html", html);
        jep.setSize(width, height);

        Graphics graphics = image.createGraphics();

        jep.print(graphics);
		
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        File file = new File("qrcode.png");
        FileUtils.writeByteArrayToFile(file, out.toByteArray());
      	
		return file;
    }

	private File base64ToImage(byte[] imageQR){

		File file = new File("qrcode.png");
		
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))){
			outputStream.write(imageQR);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return file;

	}



}
