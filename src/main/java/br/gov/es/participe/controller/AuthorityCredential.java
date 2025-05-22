package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.ForgotPasswordDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.controller.dto.UnitRolesDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.util.dto.MessageDto;
import br.gov.es.participe.util.dto.acessoCidadao.AcOrganizationInfoDto;
import br.gov.es.participe.util.dto.acessoCidadao.AcSectionInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "/authorityCredential", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorityCredential {

  private static final String SERVER = "Participe";

  @Autowired
  private PersonService personService;
  
  @Autowired
  private AcessoCidadaoService acService;

  @PutMapping
  public ResponseEntity<?> registerAuthority(
      @RequestHeader(name = "Authorization") String token) {
    if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      List<Person> people = personService.findAll();
      List<PersonDto> response = new ArrayList<>();
      people.forEach(person -> response.add(new PersonDto(person)));
      return ResponseEntity.status(200).body(response);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

}
