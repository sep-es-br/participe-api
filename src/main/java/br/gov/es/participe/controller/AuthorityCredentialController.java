package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.AuthorityCredentialRequest;
import br.gov.es.participe.controller.dto.ForgotPasswordDto;
import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonParamDto;
import br.gov.es.participe.controller.dto.PreRegistrationAuthorityDto;
import br.gov.es.participe.controller.dto.PreRegistrationDto;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.controller.dto.RelationshipAuthServiceAuxiliaryDto;
import br.gov.es.participe.controller.dto.SelfDeclarationDto;
import br.gov.es.participe.controller.dto.UnitRolesDto;
import br.gov.es.participe.model.AuthService;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PreRegistration;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.EmailService;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.service.MeetingService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.PreRegistrationService;
import br.gov.es.participe.service.QRCodeService;
import br.gov.es.participe.service.SelfDeclarationService;
import br.gov.es.participe.util.dto.MessageDto;
import br.gov.es.participe.util.dto.acessoCidadao.AcOrganizationInfoDto;
import br.gov.es.participe.util.dto.acessoCidadao.AcSectionInfoDto;
import com.google.zxing.WriterException;
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
import javax.mail.MessagingException;
import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin
@RequestMapping(value = "/authorityCredential", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorityCredentialController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private PersonService personService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private PreRegistrationService preRegistrationService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SelfDeclarationService selfDeclarationService;
    
    @Autowired
    private LocalityService localityService;
    
    @Autowired
    private AcessoCidadaoService acService;
        
    
  @PutMapping
  public ResponseEntity<PreRegistrationAuthorityDto> registerAuthority(
      @RequestHeader(name = "Authorization") String token,
      @RequestBody AuthorityCredentialRequest credentialRequest
          ) {
      
      Person madeByPerson = personService.find(credentialRequest.getMadeBy());
      
      Meeting meeting = meetingService.find(credentialRequest.getMeetingId());
      
      Locality locality = localityService.find(credentialRequest.getLocalityId());
      
      Person representedByPerson;
      if(credentialRequest.getRepresentedByCpf() == null) {
        representedByPerson = madeByPerson;
      } else {
        
        Optional<Person> optReprPerson = personService.getBySubEmail(credentialRequest.getRepresentedBySub(), credentialRequest.getRepresentedByEmail());
        
        representedByPerson = optReprPerson.orElseGet(() -> {
            Person reprPerson = new Person();
            reprPerson.setContactEmail(credentialRequest.getRepresentedByEmail());
            reprPerson.setName(credentialRequest.getRepresentedByName());
            
            AuthService as = new AuthService();
            as.setPerson(reprPerson);
            as.setServer(AcessoCidadaoService.SERVER);
            as.setServerId(credentialRequest.getRepresentedBySub());
            
            reprPerson.addAuthService(as);
            
            return personService.save(reprPerson, true);
        });
           
        
      }
      SelfDeclaration sfd = selfDeclarationService.findByPersonAndConference(representedByPerson.getId(), meeting.getConference().getId());
        
        Optional.ofNullable(sfd).ifPresentOrElse(sf -> {
            selfDeclarationService.updateLocality(sf, credentialRequest.getLocalityId());
        }, 
        () -> {
            selfDeclarationService.save(new SelfDeclaration(meeting.getConference(), locality, representedByPerson));
        }); 
        
      PreRegistration preRegistration = new PreRegistration(
              meeting, madeByPerson, representedByPerson, 
              credentialRequest.getOrganization(), credentialRequest.getRole());
      PreRegistration savedPreRegistration = preRegistrationService.save(preRegistration);
      try {
          byte[] imageQR = qrCodeService.generateQRCode(savedPreRegistration.getId().toString(), 300, 300);

        Map<String, String> emailBody = preRegistrationService.buildEmailBody(meeting);
        String[] to = new String[]{madeByPerson.getContactEmail(), representedByPerson.getContactEmail()};
        String title =  meeting.getConference().getName()+" - Pré-Credenciamento de Autoridade";
        emailService.sendEmailPreRegistration(to, title, emailBody, imageQR);
        return ResponseEntity.status(200).body(new PreRegistrationAuthorityDto(savedPreRegistration,imageQR) );
      }catch (IOException | WriterException | MessagingException ex) {
          throw new RuntimeException(ex);
      }
      


      
      
  }
  
  
      

}
