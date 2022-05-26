package br.gov.es.participe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.HighlightDto;
import br.gov.es.participe.controller.dto.HighlightParamDto;
import br.gov.es.participe.model.Highlight;
import br.gov.es.participe.service.HighlightService;

@RestController
@CrossOrigin
@RequestMapping(value = "/highlights")
public class HighlightController {

	@Autowired
	private HighlightService highlightService;

	@SuppressWarnings("rawtypes")
	@PostMapping
	public ResponseEntity store(@RequestBody HighlightParamDto highlightParamDto) {
		Highlight highlight = new Highlight(highlightParamDto);

		HighlightDto response = new HighlightDto(highlightService.save(highlight, "rem"));
		return ResponseEntity.status(200).body(response);
	}

	@SuppressWarnings("rawtypes")
	@DeleteMapping("/deleteAll/{id}")
	public ResponseEntity deleteAll(@PathVariable Long id) {
		highlightService.deleteAllByIdPerson(id);
		return ResponseEntity.status(200).build();
	}

	@SuppressWarnings("rawtypes")
	@DeleteMapping
	public ResponseEntity delete(@RequestBody HighlightParamDto highlightParamDto) {
		boolean response = highlightService.delete(new Highlight(highlightParamDto));

		return ResponseEntity.status(200).body(response);
	}
}
