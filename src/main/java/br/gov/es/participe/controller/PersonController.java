package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.ForgotPasswordDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.CookieService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.SelfDeclarationService;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import br.gov.es.participe.util.dto.MessageDto;

@RestController
@CrossOrigin
@RequestMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController {
	
	private static final String SERVER = "Participe";

	@Autowired
	private PersonService personService;
	
	@Autowired
	private TokenService tokenService;
	
	@GetMapping
    public ResponseEntity index() {
		List<Person> persons= personService.findAll();
		
		List<PersonDto> response = new ArrayList<>();
		
		persons.forEach(person -> response.add(new PersonDto(person)));
		
		return ResponseEntity.status(200).body(response); 
	}
	
	@GetMapping("/validate")
	public ResponseEntity validate(	@RequestParam(value = "email", required = false, defaultValue = "") String email,
									@RequestParam(value = "cpf", required = false, defaultValue = "") String cpf,
									@RequestParam(value = "id", required = false) Long id) {
		return ResponseEntity.status(200).body(personService.validate(email, cpf, SERVER));
	}
	
	@PostMapping
	public ResponseEntity store(@RequestBody PersonParamDto personParam) {
		if(personParam.getContactEmail().equals(personParam.getConfirmEmail())) {
			if(personService.validate(personParam.getContactEmail(), "", SERVER)) {
				Person person = personService.save(	new Person(personParam), false);
				
				SelfDeclaration self = new SelfDeclaration(personParam.getSelfDeclaretion());
				
				person = personService.complement(person, self);
				
				personService.createRelationshipWithAthService(person, 
															   personParam.getPassword(), 
															   "Participe", 
															   person.getId().toString(), 
															   self.getConference().getId());
				
				PersonDto res = new PersonDto(person);
				
				return ResponseEntity.status(200).body(res);
			}
			MessageDto msg = new MessageDto();
			msg.setMessage("E-mail já cadastrado");
			msg.setCode(403);
			return ResponseEntity.status(403).body(msg);
		}
		
		MessageDto msg = new MessageDto();
		msg.setMessage("E-mails Incompatíveis");
		msg.setCode(403);
		return ResponseEntity.status(403).body(msg);
		
	}
	
	@PostMapping("/complement")
	public ResponseEntity complement(@RequestBody PersonParamDto personParam) {
		
		if(personParam.getSelfDeclaretion() == null) 
			throw new IllegalArgumentException("Self Declaration is required");
		
		SelfDeclaration self = new SelfDeclaration(personParam.getSelfDeclaretion());
		
		Person person = personService.complement(	new Person(personParam),
													self);
		
		PersonDto response = new PersonDto(person);
		response.setSelfDeclaretion(new SelfDeclarationDto(self, false));
		return ResponseEntity.status(200).body(response);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity destroy(@PathVariable Long id) {
		personService.delete(id);
		return ResponseEntity.status(200).build();
	}
	
	@PutMapping
	public ResponseEntity update(@RequestHeader (name="Authorization") String token, @RequestBody PersonParamDto personParam) {
		String[] keys = token.split(" ");
		Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);

		if(personParam.getConfirmPassword().equals(personParam.getPassword())) {
			int size = personParam.getPassword().length();
			if(size >= 6 && size <=8 && personParam.getPassword().matches("[A-Za-z0-9]+")) {
				Person person = personService.find(idPerson);
				
				PersonDto response = new PersonDto(person);
				
				personService.createRelationshipWithAthService(person, 
															   personParam.getPassword(), 
															   SERVER, 
															   idPerson.toString(), 
															   null);
				
				return ResponseEntity.status(200).body(response);
			}
			MessageDto msg = new MessageDto();
			msg.setMessage("Senha deve conter entre 6 a 8 caracteres alfanuméricos");
			msg.setCode(403);
			return ResponseEntity.status(403).body(msg);
		}
		MessageDto msg = new MessageDto();
		msg.setMessage("Senhas fornecidas são incompatíveis");
		msg.setCode(403);
		return ResponseEntity.status(403).body(msg);
	}
	
	@PostMapping("/forgot-password")
	public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotPassword) {
		Boolean isSend = personService.forgotPassword(forgotPassword.getEmail(), forgotPassword.getConference(), SERVER);
		MessageDto msg = new MessageDto();
		
		if(isSend) {
			msg.setMessage("Nova senha enviada para "+forgotPassword.getEmail());
			msg.setCode(200);
			return ResponseEntity.status(200).body(msg);
		}
		msg.setMessage("Hummm... Não encontramos esse e-mail em nossos registros. Talvez você tenha se cadastrado com outro endereço ou utilizado o Acesso Cidadão, Google ou redes sociais");
		msg.setCode(403);
		return ResponseEntity.status(403).body(msg);
	}
}
