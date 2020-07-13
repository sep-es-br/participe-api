package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Highlight;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.service.*;
import br.gov.es.participe.util.domain.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@CrossOrigin
@RequestMapping(value = "/participation", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParticipationController {

	@Autowired
	private ParticipationService participationService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private HighlightService highlightService;
	
	@Autowired
	private PlanItemService planItemService;
	
	@GetMapping("/plan-item/{idConference}")
	public ResponseEntity<BodyParticipationDto> getBody(@RequestHeader (name="Authorization") String token,
														@RequestParam(name = "text", required = false, defaultValue="") String text,
														@RequestParam(name = "idLocality", required = true) Long idLocality,
														@RequestParam(name = "idPlanItem", required = false) Long idPlanItem, 
														@PathVariable Long idConference, 
														UriComponentsBuilder uriComponentsBuilder) {
		String[] keys = token.split(" ");
		Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);
		
		BodyParticipationDto body = participationService.body(idPlanItem, idLocality, idConference, idPerson, text, uriComponentsBuilder);
		if(body.getItens() != null) {
			body.getItens().sort((i1,i2)-> i1.getName().trim().compareToIgnoreCase(i2.getName().trim()));
		}
		return ResponseEntity.status(200).body(body);
	}
	
	@GetMapping("/portal-header/{idConference}")
	public ResponseEntity<PortalHeader> getHeader(@PathVariable Long idConference, UriComponentsBuilder uriComponentsBuilder){
		 PortalHeader header = participationService.header(idConference, uriComponentsBuilder);
		 return ResponseEntity.status(200).body(header); 
	}
	
	@PostMapping("/highlights")
	public ResponseEntity<PlanItemDto> createComment(@RequestHeader (name="Authorization") String token, 
													 @RequestBody CommentParamDto commentParamDto){
		String[] keys = token.split(" ");
		Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);
		Person person = new Person();
		person.setId(idPerson);
		
		PlanItemDto response = null;
		if(commentParamDto.getText() != null) {
			Comment comment = new Comment(commentParamDto);
			comment.setPersonMadeBy(person);
			commentService.save(comment, null, "rem", true);
		}
		else {
			Highlight highlight = new Highlight();
			Comment comment = new Comment(commentParamDto);
			highlight.setLocality(comment.getLocality());
			highlight.setPlanItem(comment.getPlanItem());
			highlight.setPersonMadeBy(person);
			highlight.setConference(comment.getConference());
			highlightService.save(highlight, "rem");
		}
		
		PlanItem planItem = planItemService.find(commentParamDto.getPlanItem());
		response = participationService.generatePlanItemDtoFront(planItem, idPerson, commentParamDto.getConference(), commentParamDto.getLocality());
		response.setStructureItem(null);
		
		return ResponseEntity.status(200).body(response); 
	}
	
	@PostMapping("/alternative-proposal")
	public ResponseEntity<CommentDto> alternativeProposal(@RequestHeader (name="Authorization") String token, @RequestBody CommentParamDto commentParamDto){
		String[] keys = token.split(" ");
		Long idPerson = tokenService.getPersonId(keys[1], TokenType.AUTHENTICATION);
		Person person = new Person();
		person.setId(idPerson);
		
		Comment comment = new Comment(commentParamDto);
		comment.setPersonMadeBy(person);
		CommentDto response = new CommentDto(commentService.save(comment, null, "rem", true), false, true);
		response.setTime(null);
		response.setFrom(null);
		response.setType(null);
		response.setStatus(null);
		
		return ResponseEntity.status(200).body(response);
	}
	
	@GetMapping("/conference/{id}")
	public ResponseEntity<ConferenceDto> findAll(@PathVariable Long id, UriComponentsBuilder uriComponentsBuilder) {
		ConferenceDto conferenceDto = participationService.getConferenceDto(id, uriComponentsBuilder);		
		return ResponseEntity.status(200).body(conferenceDto);
	}
}