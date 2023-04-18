package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.PersonProfileEmailsDto;
import br.gov.es.participe.controller.dto.PersonProfileSearchDto;
import br.gov.es.participe.controller.dto.PersonProfileUpdateDto;
import br.gov.es.participe.service.MergePersonProfileService;
import br.gov.es.participe.service.PersonProfileService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.TokenService;
import br.gov.es.participe.util.domain.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/person/profile", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonProfileController {

  private final PersonService personService;
  private final PersonProfileService personProfileService;
  private final MergePersonProfileService mergePersonProfileService;
  private final TokenService tokenService;

  @Autowired
  public PersonProfileController(
      PersonService personService,
      PersonProfileService personProfileService,
      MergePersonProfileService mergePersonProfileService,
      TokenService tokenService) {
    this.personService = personService;
    this.personProfileService = personProfileService;
    this.mergePersonProfileService = mergePersonProfileService;
    this.tokenService = tokenService;
  }

  @GetMapping("/{personId}")
  public ResponseEntity<PersonProfileSearchDto> findPersonById(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long personId,
      @Param("conferenceId") Long conferenceId) {

    if ((personService.getPerson(token).getId().equals(personId))
        || (personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))) {
      PersonProfileSearchDto person = personProfileService.findById(personId, conferenceId);
      return ResponseEntity.ok(person);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }


  @Transactional
  @PutMapping("/merge/{personIdToMerge}")
  public ResponseEntity<PersonParamDto> findPersonById(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long personIdToMerge) {

    Long idPerson = personService.getPerson(token).getId();
    if ((idPerson.equals(personIdToMerge))
        || (personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))) {
      PersonParamDto person = this.mergePersonProfileService.merge(personIdToMerge, idPerson);
      return ResponseEntity.ok(person);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

  @Transactional
  @PutMapping("/{personId}")
  public ResponseEntity<PersonProfileSearchDto> updatePerson(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long personId,
      @RequestBody PersonProfileUpdateDto personDto) {

    if ((personId.equals(personService.getPerson(token).getId()))
        || (personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))) {
      personDto.setId(personId);
      PersonProfileSearchDto person = personProfileService.updatePersonProfile(personDto);
      return ResponseEntity.ok(person);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

  @GetMapping("/{personId}/emails")
  public ResponseEntity<List<PersonProfileEmailsDto>> findPersonEmails(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long personId) {
    if ((personId.equals(personService.getPerson(token).getId()))
        || (personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))) {
      List<PersonProfileEmailsDto> emails = personProfileService.findPersonEmail(personId);
      return ResponseEntity.ok(emails);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

}
