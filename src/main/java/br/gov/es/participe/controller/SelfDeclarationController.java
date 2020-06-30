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
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.SelfDeclarationService;

@RestController
@CrossOrigin
@RequestMapping(value = "/selfdeclarations", produces = MediaType.APPLICATION_JSON_VALUE)
public class SelfDeclarationController {

	@Autowired
	private SelfDeclarationService selfDeclarationService;
	
	@GetMapping("/{id}")
	public ResponseEntity findAll(@PathVariable Long id) {
		List<SelfDeclaration> selfDeclaraions = selfDeclarationService.findAll(id);
		List<SelfDeclarationDto> response = new ArrayList<>();
		
		selfDeclaraions.forEach(self -> response.add(new SelfDeclarationDto(self, true)));
		
		return ResponseEntity.status(200).body(response);
	}
	
	@PostMapping
	public ResponseEntity store(@RequestBody SelfDeclarationDto selfDeclarationDto) {
		SelfDeclaration selfDeclaraion = new SelfDeclaration(selfDeclarationDto);
		SelfDeclarationDto response = new SelfDeclarationDto(selfDeclarationService.save(selfDeclaraion),true);
		return ResponseEntity.status(200).body(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity delete(@PathVariable Long id) {
		selfDeclarationService.delete(id);
		return ResponseEntity.status(200).build();
	}
}
