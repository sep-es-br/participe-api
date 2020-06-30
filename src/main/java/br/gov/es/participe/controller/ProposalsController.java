package br.gov.es.participe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.ProposalDto;
import br.gov.es.participe.controller.dto.ProposalsDto;
import br.gov.es.participe.controller.dto.ProposalsFilterDto;
import br.gov.es.participe.service.CommentService;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.service.ProposalsService;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;

@RestController
@CrossOrigin
@RequestMapping(value = "/proposals", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProposalsController {
	
	@Autowired
	private ProposalsService proposalsService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private TokenService tokenService;

	@GetMapping("/filters/{idConference}")
	public ResponseEntity<ProposalsFilterDto> getFilters(@PathVariable Long idConference){
		ProposalsFilterDto response = proposalsService.getFilters(idConference);
		
		return ResponseEntity.status(200).body(response);
	}
	
	@GetMapping("/{idConference}")
	public ResponseEntity<ProposalsDto> listProposal(@PathVariable Long idConference,
									   @RequestHeader (name="Authorization") String token,
									   @RequestParam(name = "text", required = false, defaultValue="") String text,
									   @RequestParam(name = "localityIds", required = false) Long[] localityIds,
									   @RequestParam(name = "planItemIds", required = false) Long[] planItemIds,
									   @RequestParam(name = "pageNumber", required = true) Integer pageNumber) {
		Long[] emptyList = {};
		String[] keys = token.split(" ");
		Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);
		
		 ProposalsDto response = commentService.listProposal(idConference, 
																 idPerson,
																 pageNumber, 
																 text,
																 "pub", 
																 localityIds != null ? localityIds : emptyList,
																 planItemIds != null ? planItemIds : emptyList);
		return ResponseEntity.status(200).body(response);
	}
	
	@GetMapping("/likes/{idComment}")
	public ResponseEntity<Integer> makeLike(@RequestHeader (name="Authorization") String token, @PathVariable Long idComment ){
		String[] keys = token.split(" ");
		Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);
		
		Integer response = proposalsService.makeLike(idPerson, idComment);
		return ResponseEntity.status(200).body(response);
	}
}
