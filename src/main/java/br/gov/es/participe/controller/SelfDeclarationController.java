package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.SelfDeclarationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/selfdeclarations", produces = MediaType.APPLICATION_JSON_VALUE)
public class SelfDeclarationController {

	@Autowired
	private SelfDeclarationService selfDeclarationService;

	@Autowired
	private PersonService personService;

	@GetMapping("/{id}")
	@SuppressWarnings("rawtypes")
	public ResponseEntity findAll(
			@RequestHeader(name = "Authorization") String token,
			@PathVariable Long id) {

		if ((id.equals(personService.getPerson(token).getId()))
				|| (personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))) {
			List<SelfDeclaration> selfDeclaraions = selfDeclarationService.findAllByPerson(id);
			List<SelfDeclarationDto> response = new ArrayList<>();

			selfDeclaraions.forEach(self -> response.add(new SelfDeclarationDto(self, true)));

			return ResponseEntity.status(200).body(response);
		} else {
			return ResponseEntity.status(401).body(null);
		}
	}

	@Transactional
	@PostMapping
	@SuppressWarnings("rawtypes")
	public ResponseEntity store(
			@RequestHeader(name = "Authorization") String token,
			@RequestBody SelfDeclarationDto selfDeclarationDto) {

		if ((selfDeclarationDto.getPerson().getId().equals(personService.getPerson(token).getId()))
				|| (personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))) {
			SelfDeclaration selfDeclaraion = new SelfDeclaration(selfDeclarationDto);
			SelfDeclarationDto response = new SelfDeclarationDto(selfDeclarationService.save(selfDeclaraion), true);
			return ResponseEntity.status(200).body(response);
		} else {
			return ResponseEntity.status(401).body(null);
		}
	}
 

	@Transactional
	@DeleteMapping("/{id}")
	@SuppressWarnings("rawtypes")
	public ResponseEntity delete(
			@RequestHeader(name = "Authorization") String token,
			@PathVariable Long id) {

		if ((id.equals(personService.getPerson(token).getId()))
				|| (personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))) {
			selfDeclarationService.delete(id);
			return ResponseEntity.status(200).build();
		} else {
			return ResponseEntity.status(401).body(null);
		}
	}
	
}
