package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.ForgotPasswordDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.util.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController {

  private static final String SERVER = "Participe";

  @Autowired
  private PersonService personService;

  @GetMapping
  @SuppressWarnings("rawtypes")
  public ResponseEntity index(
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

  @PostMapping("/operator")
  @SuppressWarnings("rawtypes")
  public ResponseEntity operator(
      @RequestHeader(name = "Authorization") String token,
      @RequestParam(value = "profile", required = false, defaultValue = "") String profile,
      @RequestBody PersonParamDto personParam) throws IOException {
    if (!(personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))) {
      return ResponseEntity.status(401).body(null);
    }
    if (!profile.equalsIgnoreCase("Administrator") &&
        !profile.equalsIgnoreCase("Moderator") &&
        !profile.equalsIgnoreCase("Recepcionist")) {
      return ResponseEntity.status(404).body(null);
    }
    Optional<Person> person = personService.findByContactEmail(personParam.getContactEmail());
    if (!person.isPresent()) {
      Person addedPerson = personService.storePersonOperator(personParam, profile);
      if (addedPerson == null) {
        MessageDto msg = new MessageDto();
        msg.setMessage("Impossível adicionar o usuário");
        msg.setCode(422);
        return ResponseEntity.status(422).body(msg);
      } else {
        return ResponseEntity.status(200).body(addedPerson);
      }
    } else {
      return ResponseEntity.status(200).body(person);
    }
  }

  @GetMapping("/validate")
  @SuppressWarnings("rawtypes")
  public ResponseEntity validate(
      @RequestParam(value = "email", required = false, defaultValue = "") String email,
      @RequestParam(value = "cpf", required = false, defaultValue = "") String cpf,
      @RequestParam(value = "id", required = false) Long id) {
    return ResponseEntity
        .status(200)
        .body(personService.validate(email, cpf, SERVER));
  }

  @PostMapping
  @SuppressWarnings("rawtypes")
  public ResponseEntity store(
      @RequestBody PersonParamDto personParam) {
    return personService.storePerson(personParam, false);
  }

  @PostMapping("/complement")
  @SuppressWarnings("rawtypes")
  public ResponseEntity complement(
      @RequestHeader(name = "Authorization") String token,
      @RequestBody PersonParamDto personParam) {

    if (personService.getPerson(token).getId().equals(personParam.getId())) {

      if (personParam.getSelfDeclaration() == null) {
        throw new IllegalArgumentException("Self Declaration is required");
      }

      SelfDeclaration self = new SelfDeclaration(personParam.getSelfDeclaration());
      Person person = personService.complement(
          new Person(personParam),
          self);

      PersonDto response = new PersonDto(person);
      response.setSelfDeclaretion(new SelfDeclarationDto(self, false));
      return ResponseEntity.status(200).body(response);
    } else {
      return ResponseEntity.status(401).body(null);
    }

  }

  
    @DeleteMapping("/delete/{id}")
    @SuppressWarnings("rawtypes")
      public ResponseEntity destroy(@RequestHeader(name = "Authorization") String token,@PathVariable Long id) {
      
      if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))
      {
      personService.delete(id);
      return ResponseEntity.status(200).build();
      } else {
      return ResponseEntity.status(401).body(null);
      }
    
    }
   
  @PutMapping("/{personId}")
  @SuppressWarnings("rawtypes")
  public ResponseEntity update(
      @RequestHeader(name = "Authorization") String token,
      @RequestBody PersonParamDto personParam,
      @PathVariable(name = "personId") Long personId) {

    if ((personService.hasOneOfTheRoles(token, new String[] { "Administrator" }))
        || (personService.getPerson(token).getId().equals(personParam.getId()))) {
      personParam.setId(personId);
      return personService.updatePerson(token, personParam, false);
    } else {
      return ResponseEntity.status(401).body(null);
    }
  }

  @PostMapping("/forgot-password")
  @SuppressWarnings("rawtypes")
  public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotPassword) {
    Boolean isSend = personService.forgotPassword(forgotPassword.getEmail(), forgotPassword.getConference(), SERVER);
    MessageDto msg = new MessageDto();

    if (isSend) {
      msg.setMessage("Nova senha enviada para " + forgotPassword.getEmail());
      msg.setCode(200);
      return ResponseEntity.status(200).body(msg);
    }
    msg.setMessage(
        "Hummm... Não encontramos esse e-mail em nossos registros. Talvez você tenha se cadastrado com outro endereço ou utilizado o Acesso Cidadão, Google ou Facebook");
    msg.setCode(403);
    return ResponseEntity.status(403).body(msg);
  }

}
