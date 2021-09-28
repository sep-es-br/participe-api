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

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController {

  private static final String SERVER = "Participe";

  @Autowired
  private PersonService personService;

  @GetMapping
  public ResponseEntity index() {
    List<Person> persons = personService.findAll();

    List<PersonDto> response = new ArrayList<>();

    persons.forEach(person -> response.add(new PersonDto(person)));

    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("/validate")
  public ResponseEntity validate(
      @RequestParam(value = "email", required = false, defaultValue = "") String email,
      @RequestParam(value = "cpf", required = false, defaultValue = "") String cpf,
      @RequestParam(value = "id", required = false) Long id
  ) {
    return ResponseEntity
        .status(200)
        .body(personService.validate(email, cpf, SERVER));
  }

  @PostMapping
  public ResponseEntity store(@RequestBody PersonParamDto personParam) {
    return personService.storePerson(personParam, false);
  }

  @PostMapping("/complement")
  public ResponseEntity complement(@RequestBody PersonParamDto personParam) {

    if (personParam.getSelfDeclaration() == null) {
      throw new IllegalArgumentException("Self Declaration is required");
    }

    SelfDeclaration self = new SelfDeclaration(personParam.getSelfDeclaration());
    Person person = personService.complement(
        new Person(personParam),
        self
    );

    PersonDto response = new PersonDto(person);
    response.setSelfDeclaretion(new SelfDeclarationDto(self, false));
    return ResponseEntity.status(200).body(response);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity destroy(@PathVariable Long id) {
    personService.delete(id);
    return ResponseEntity.status(200).build();
  }

  @PutMapping("/{personId}")
  public ResponseEntity update(@RequestHeader(name = "Authorization") String token,
                               @RequestBody PersonParamDto personParam,
                               @PathVariable(name = "personId") Long personId) {
    personParam.setId(personId);
    return personService.updatePerson(token, personParam, false);
  }

  @PostMapping("/forgot-password")
  public ResponseEntity forgotPassword(@RequestBody ForgotPasswordDto forgotPassword) {
    Boolean isSend = personService.forgotPassword(forgotPassword.getEmail(), forgotPassword.getConference(), SERVER);
    MessageDto msg = new MessageDto();

    if (isSend) {
      msg.setMessage("Nova senha enviada para " + forgotPassword.getEmail());
      msg.setCode(200);
      return ResponseEntity.status(200).body(msg);
    }
    msg.setMessage(
        "Hummm... Não encontramos esse e-mail em nossos registros. Talvez você tenha se cadastrado com outro endereço ou utilizado o Acesso Cidadão, Google ou redes sociais");
    msg.setCode(403);
    return ResponseEntity.status(403).body(msg);
  }
}
