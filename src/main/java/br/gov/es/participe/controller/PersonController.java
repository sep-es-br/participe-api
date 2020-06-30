package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.SelfDeclarationService;

@RestController
@CrossOrigin
@RequestMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController {

	@Autowired
	private PersonService personService;
	
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
		return ResponseEntity.status(200).body(personService.validate(email, cpf, id));
	}
	
	@PostMapping
	public ResponseEntity store(@RequestBody PersonParamDto personParam) {
		Person person = personService.save(	new Person(personParam), false);
		
		PersonDto response = new PersonDto(person);
		
		return ResponseEntity.status(200).body(response);
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
	
	@DeleteMapping("/{id}")
	public ResponseEntity destroy(@PathVariable Long id) {
		personService.delete(id);
		return ResponseEntity.status(200).build();
	}
	
	@PutMapping
	public ResponseEntity update(@RequestBody PersonParamDto personParam) {
		SelfDeclaration self = new SelfDeclaration(personParam.getSelfDeclaretion());
		
		Person person = personService.complement(	new Person(personParam),
													self);
		
		PersonDto response = new PersonDto(person);
		response.setSelfDeclaretion(new SelfDeclarationDto(self, false));
		return ResponseEntity.status(200).body(response);
	}
}
