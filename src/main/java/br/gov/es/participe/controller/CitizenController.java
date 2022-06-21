package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.LocalityCitizenSelectDto;
import br.gov.es.participe.controller.dto.PersonKeepCitizenDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.util.dto.MessageDto;
import br.gov.es.participe.util.interfaces.ApiPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/citizen", produces = MediaType.APPLICATION_JSON_VALUE)
public class CitizenController {

  @Autowired
  private PersonService personService;

  @Autowired
  private LocalityService localityService;

  @ApiPageable
  @GetMapping
  public ResponseEntity<Page<PersonKeepCitizenDto>> listCitizen(
      @RequestHeader("Authorization") String token,
      @RequestParam(value = "name", required = false, defaultValue = "") String name,
      @RequestParam(value = "email", required = false, defaultValue = "") String email,
      @RequestParam(value = "autentication", required = false, defaultValue = "") String autentication,
      @RequestParam(value = "status", required = false, defaultValue = "") Boolean active,
      @RequestParam(value = "locality", required = false, defaultValue = "") List<Long> locality,
      @RequestParam(value = "conferenceId", required = false) Long conferenceId,
      @ApiIgnore Pageable page) {

    if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      Page<PersonKeepCitizenDto> list = personService
          .listKeepCitizen(name, email, autentication, active, locality, conferenceId, page);
      return ResponseEntity.status(200).body(list);
    } else {
      return ResponseEntity.status(401).body(null);
    }

  }

  @GetMapping("/{personId}")
  public ResponseEntity<PersonKeepCitizenDto> getCitizenById(
      @RequestHeader("Authorization") String token,
      @PathVariable Long personId,
      @RequestParam Long conferenceId) {

    if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      PersonKeepCitizenDto citizen = personService.findCitizenById(personId, conferenceId);
      return ResponseEntity.status(200).body(citizen);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

  @SuppressWarnings({ "rawtypes" })
  @PostMapping
  public ResponseEntity store(
      @RequestHeader("Authorization") String token,
      @RequestBody PersonParamDto personParam) {

    if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      return personService.storePerson(personParam, false);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

  @SuppressWarnings({ "rawtypes" })
  @DeleteMapping("/{id}")
  public ResponseEntity destroy(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long id) {

    if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      personService.delete(id);
      return ResponseEntity.status(200).build();
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

  @SuppressWarnings({ "rawtypes" })
  @PutMapping("/{id}")
  public ResponseEntity update(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable Long id,
      @RequestBody PersonParamDto personParam) {

    if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      personParam.setId(id);
      return personService.updatePerson(personParam, false);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

  @GetMapping("/localities/{idConferences}")
  public ResponseEntity<LocalityCitizenSelectDto> getLocalities(
      @RequestHeader(name = "Authorization") String token,
      @PathVariable(name = "idConferences", required = false) Long idConferences,
      @RequestParam(name = "name", required = false, defaultValue = "") String name) {

    if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      LocalityCitizenSelectDto localityCitizenSelectDto = localityService.getLocalitiesToDisplay(idConferences, name);
      return ResponseEntity.status(200).body(localityCitizenSelectDto);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }
}
