package br.gov.es.participe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.HighlightDto;
import br.gov.es.participe.controller.dto.HighlightParamDto;
import br.gov.es.participe.model.Highlight;
import br.gov.es.participe.service.HighlightService;
import br.gov.es.participe.service.PersonService;

@RestController
@CrossOrigin
@RequestMapping(value = "/highlights")
public class HighlightController {

	@Autowired
	private PersonService personService;

	@Autowired
	private HighlightService highlightService;

	@SuppressWarnings("rawtypes")
	@Transactional
	@PostMapping
	public ResponseEntity store(
			@RequestHeader("Authorization") String token,
			@RequestBody HighlightParamDto highlightParamDto) {

		if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
			return ResponseEntity.status(401).body(null);
		}

		Highlight highlight = new Highlight(highlightParamDto);

		HighlightDto response = new HighlightDto(highlightService.save(highlight, "rem"));
		return ResponseEntity.status(200).body(response);
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	@DeleteMapping("/deleteAll/{id}")
	public ResponseEntity deleteAll(
			@RequestHeader("Authorization") String token,
			@PathVariable Long id) {
		if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
			return ResponseEntity.status(401).body(null);
		}
		highlightService.deleteAllByIdPerson(id);
		return ResponseEntity.status(200).build();
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	@DeleteMapping
	public ResponseEntity delete(
			@RequestHeader("Authorization") String token,
			@RequestBody HighlightParamDto highlightParamDto) {
		if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
			return ResponseEntity.status(401).body(null);
		}
		boolean response = highlightService.delete(new Highlight(highlightParamDto));

		return ResponseEntity.status(200).body(response);
	}
	
}
