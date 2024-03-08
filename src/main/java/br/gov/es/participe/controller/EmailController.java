package br.gov.es.participe.controller;


import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.gov.es.participe.service.EmailService;

@RestController
@RequestMapping(value = "/email")
public class EmailController {
    
    @Autowired
    EmailService emailService;
    private static final String MICROREGIAO = "microregiao";
    private static final String LOCAL = "localDaReuniao";

    @GetMapping
    public ResponseEntity<String>sendEmail() throws MessagingException{
        String to = "brunomatosbarbosa@hotmail.com";
        String title = "Teste";
        Map<String, String> data = new HashMap<>();
        
        data.put(MICROREGIAO, "Nordeste e Noroeste");
        data.put(LOCAL, "Colatina ");

        emailService.sendEmailPreRegistration(to, title, data);

        return ResponseEntity.ok().body("email enviado") ;
    }
}
