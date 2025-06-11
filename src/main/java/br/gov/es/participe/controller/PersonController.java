package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.ForgotPasswordDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.controller.dto.UnitRolesDto;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.SelfDeclarationService;
import br.gov.es.participe.util.dto.MessageDto;
import br.gov.es.participe.util.dto.acessoCidadao.AcOrganizationInfoDto;
import br.gov.es.participe.util.dto.acessoCidadao.AcSectionInfoDto;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jdk.jfr.ContentType;

@RestController
@CrossOrigin
@RequestMapping(value = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
public class PersonController {

  private static final String SERVER = "Participe";

  @Autowired
  private PersonService personService;
  
  @Autowired
  private AcessoCidadaoService acService;
  
  @Autowired
  private LocalityService localityService;
  
  @Autowired
  private SelfDeclarationService selfDeclarationService;

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
  
  @GetMapping("{idPerson}/ACRole/{idConference}")
  public ResponseEntity<?> getACRole(
          @PathVariable Long idPerson,
          @PathVariable Long idConference
  ){
      
      HashMap<String, Object> acRole = new HashMap<>();
      
      String sub = personService.getSubById(idPerson);
      if(sub == null) return ResponseEntity.ok(null);
      
      acRole.put("sub", sub);  
      
      UnitRolesDto role = acService.findPriorityRoleFromAcessoCidadaoAPIBySub(sub, false);
      if(role != null) acRole.put("role", role.getNome());  
      
      if(role != null){
        AcSectionInfoDto sectionInfoDto = acService.findSectionInfoFromOrganogramaAPI(role.getLotacaoGuid());
        if(sectionInfoDto != null) {
            AcOrganizationInfoDto organizationInfoDto = acService.findOrganizationInfoFromOrganogramaAPI(sectionInfoDto.getGuidOrganizacao());
            if(organizationInfoDto != null) {
                acRole.put("organization", organizationInfoDto.getRazaoSocial());
                acRole.put("organizationShort", organizationInfoDto.getSigla());
            }
        }
      }
      
      
      PublicAgentDto paDto = new PublicAgentDto();
      paDto.setSub(sub);
      
      paDto = acService.findThePersonEmailBySubInAcessoCidadaoAPI(paDto);
      
      SelfDeclaration sf = selfDeclarationService.findByPersonAndConference(idPerson, idConference);
      
      Optional.ofNullable(sf).ifPresent(_sf -> {
        acRole.put("localityId", _sf.getLocality().getId());
      });
     
      Optional.ofNullable(paDto).ifPresent(_paDto -> {
          acRole.put("email", _paDto.getEmail());
      });
        
      return ResponseEntity.ok(acRole);
  }
  
  @GetMapping("subById/{idPerson}")
  public ResponseEntity<?> getSubById(
          @PathVariable Long idPerson
  ){
      return ResponseEntity.ok(Map.of("sub", personService.getSubById(idPerson)));
      
  }
  
  @GetMapping("{cpf}/ACInfoByCpf/{conferenceId}")
  public ResponseEntity<?> getACInfoByCpf(
          @PathVariable String cpf,
          @PathVariable Long conferenceId
  ){
      HashMap<String, Object> acInfo = new HashMap<>();
      
      PublicAgentDto publicAgentDto = acService.findSubFromPersonInAcessoCidadaoAPIByCpf(cpf);
      String sub = publicAgentDto.getSub();
      
      acInfo.put("authoritySub", sub);
      
      
        PublicAgentDto person = new PublicAgentDto();
        person.setSub(sub);
        person = acService.findThePersonEmailBySubInAcessoCidadaoAPI(person);

        if(person.getEmail() != null) acInfo.put("email", person.getEmail());
                
      
      PublicAgentDto maybePublicAgentDto = acService.findAgentPublicBySubInAcessoCidadaoAPI(sub);
      if(maybePublicAgentDto != null) {
          acInfo.put("name", maybePublicAgentDto.getName());
          UnitRolesDto role = acService.findPriorityRoleFromAcessoCidadaoAPIBySub(sub, false);
          if(role != null) acInfo.put("role", role.getNome());
               
      } else {
          personService.getBySubEmail(sub, person.getEmail()).ifPresent(_person -> {
              acInfo.put("name", _person.getName());
          });
          
      }
      
        Optional<Person> optPerson = personService.getBySubEmail(sub, person.getEmail());

        optPerson.ifPresent(p -> {
            SelfDeclaration sd = selfDeclarationService.findByPersonAndConference(p.getId(), conferenceId);

            if(sd != null) {
                acInfo.put("localityId", sd.getLocality().getId());
            }

        });
      
            
      return ResponseEntity.ok(acInfo);
  }

  @Transactional
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


  @Transactional
  @PostMapping
  @SuppressWarnings("rawtypes")
  public ResponseEntity store(
      @RequestBody PersonParamDto personParam) {
    return personService.storePerson(personParam, false);
  }


  @Transactional
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
          self
      );

      PersonDto response = new PersonDto(person);
      response.setSelfDeclaretion(new SelfDeclarationDto(self, false));
      return ResponseEntity.status(200).body(response);
    } else {
      return ResponseEntity.status(401).body(null);
    }

  }

  @Transactional
  @DeleteMapping("/delete/{id}")
  @SuppressWarnings("rawtypes")
  public ResponseEntity destroy(@RequestHeader(name = "Authorization") String token,@PathVariable Long id) {

    if (personService.hasOneOfTheRoles(token, new String[] { "Administrator" })) {
      personService.delete(id);
      return ResponseEntity.status(200).build();
    }
    else {
      return ResponseEntity.status(401).body(null);
    }

  }

  @Transactional
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


  @Transactional
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
