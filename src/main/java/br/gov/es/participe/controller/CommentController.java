package br.gov.es.participe.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.participe.controller.dto.CommentDto;
import br.gov.es.participe.controller.dto.CommentParamDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.service.CommentService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;

@RestController
@CrossOrigin
@ComponentScan(basePackages = { "br.gov.es.participe.service.CommentService" })
@RequestMapping(value = "/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private PersonService personService;

	@GetMapping
	public ResponseEntity<List<CommentDto>> index(
			@RequestHeader(name = "Authorization") String token,
			@RequestParam(value = "idPerson", required = false, defaultValue = "") Long idPerson,
			@RequestParam(value = "idConference", required = false) Long idConference) {
		if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
			return ResponseEntity.status(401).body(null);
		}
		List<Comment> comments = commentService.findAll(idPerson, idConference);
		List<CommentDto> response = new ArrayList<>();
		comments.forEach(comment -> response.add(new CommentDto(comment, false)));

		return ResponseEntity.status(200).body(response);
	}

	@Transactional
	@PostMapping
	public ResponseEntity<CommentDto> store(
			@RequestHeader(name = "Authorization") String token,
			@RequestBody CommentParamDto commentParamDto) {

		String[] chave = token.split(" ");
		Long idPerson = tokenService.getPersonId(chave[1], TokenType.AUTHENTICATION);
		Comment comment = new Comment(commentParamDto);
		Person person = new Person();
		person.setId(idPerson);
		comment.setPersonMadeBy(person);
		CommentDto response = new CommentDto(commentService.save(comment, null, true), false);

		return ResponseEntity.status(200).body(response);
	}

	@Transactional
	@PostMapping("/fatherPlanItem")
	public ResponseEntity<CommentDto> storeFatherPlanItemNode(
			@RequestHeader("Authorization") String token,
			@RequestBody CommentParamDto commentParamDto) {
		if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator", "Moderator" })) {
			return ResponseEntity.status(401).body(null);
		}
		Comment comment = new Comment(commentParamDto);
		CommentDto response = new CommentDto(commentService.save(comment, null, false), false);

		return ResponseEntity.status(200).body(response);
	}

 /* 

	@DeleteMapping("/deleteAll/{id}")
	public ResponseEntity<Void> delete(
			@RequestHeader("Authorization") String token,
			@PathVariable Long id) {
		if (!personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
			return ResponseEntity.status(401).body(null);
		}
		commentService.deleteAllByIdPerson(id);
		return ResponseEntity.status(200).build();
	}

*/	
}