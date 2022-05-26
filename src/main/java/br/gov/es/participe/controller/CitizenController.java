package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.LocalityCitizenSelectDto;
import br.gov.es.participe.controller.dto.PersonKeepCitizenDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.service.PersonService;
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
      @RequestParam(value = "name", required = false, defaultValue = "") String name,
      @RequestParam(value = "email", required = false, defaultValue = "") String email,
      @RequestParam(value = "autentication", required = false, defaultValue = "") String autentication,
      @RequestParam(value = "status", required = false, defaultValue = "") Boolean active,
      @RequestParam(value = "locality", required = false, defaultValue = "") List<Long> locality,
      @RequestParam(value = "conferenceId", required = false) Long conferenceId,
      @ApiIgnore Pageable page) {
    Page<PersonKeepCitizenDto> list = personService
        .listKeepCitizen(name, email, autentication, active, locality, conferenceId, page);
    return ResponseEntity.status(200).body(list);
  }

  @GetMapping("/{personId}")
  public ResponseEntity<PersonKeepCitizenDto> getCitizenById(@PathVariable Long personId,
      @RequestParam Long conferenceId) {
    PersonKeepCitizenDto citizen = personService.findCitizenById(personId, conferenceId);
    return ResponseEntity.status(200).body(citizen);
  }

  @SuppressWarnings({ "rawtypes" })
  @PostMapping
  public ResponseEntity store(@RequestBody PersonParamDto personParam) {
    return personService.storePerson(personParam, false);
  }

  @SuppressWarnings({ "rawtypes" })
  @DeleteMapping("/{id}")
  public ResponseEntity destroy(@PathVariable Long id) {
    personService.delete(id);
    return ResponseEntity.status(200).build();
  }

  @SuppressWarnings({ "rawtypes" })
  @PutMapping("/{id}")
  public ResponseEntity update(@RequestHeader(name = "Authorization") String token, @PathVariable Long id,
      @RequestBody PersonParamDto personParam) {
    personParam.setId(id);
    return personService.updatePerson(personParam, false);
  }

  @GetMapping("/localities/{idConferences}")
  public ResponseEntity<LocalityCitizenSelectDto> getLocalities(
      @PathVariable(name = "idConferences", required = false) Long idConferences,
      @RequestParam(name = "name", required = false, defaultValue = "") String name) {
    LocalityCitizenSelectDto localityCitizenSelectDto = localityService.getLocalitiesToDisplay(idConferences, name);
    return ResponseEntity.status(200).body(localityCitizenSelectDto);
  }
}
