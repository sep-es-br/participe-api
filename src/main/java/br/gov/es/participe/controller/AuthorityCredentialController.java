package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.AuthorityCredentialRequest;
import br.gov.es.participe.controller.dto.CheckedInAtDto;
import br.gov.es.participe.controller.dto.PreRegistrationAuthorityDto;
import br.gov.es.participe.controller.dto.PublicAgentDto;
import br.gov.es.participe.model.AuthService;
import br.gov.es.participe.model.Locality;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PreRegistration;
import br.gov.es.participe.model.SelfDeclaration;
import br.gov.es.participe.service.AcessoCidadaoService;
import br.gov.es.participe.service.AuthorityCredentialService;
import br.gov.es.participe.service.EmailService;
import br.gov.es.participe.service.LocalityService;
import br.gov.es.participe.service.MeetingService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.PreRegistrationService;
import br.gov.es.participe.service.QRCodeService;
import br.gov.es.participe.service.SelfDeclarationService;
import com.google.zxing.WriterException;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    @Autowired
    private AuthorityCredentialService authcSrv;
        
    
    @PutMapping
    public ResponseEntity<PreRegistrationAuthorityDto> registerAuthority(
        @RequestHeader(name = "Authorization") String token,
        @RequestBody AuthorityCredentialRequest credentialRequest
            ) {

        Person madeByPerson = personService.find(credentialRequest.getMadeBy());

        PublicAgentDto dumb = new PublicAgentDto();
        dumb.setSub(personService.getSubById(madeByPerson.getId()));

        PublicAgentDto publicAgent = this.acService.findThePersonEmailBySubInAcessoCidadaoAPI(dumb);

        madeByPerson.setContactEmail(publicAgent.getCorporativo() != null ? publicAgent.getCorporativo() : publicAgent.getEmail()  );

        Meeting meeting = meetingService.find(credentialRequest.getMeetingId());

        Locality locality = localityService.find(credentialRequest.getLocalityId());

        Person representedByPerson;
        if(credentialRequest.getRepresentedByCpf() == null) {
          representedByPerson = madeByPerson;
        } else {

          Optional<Person> optReprPerson = personService.findByLoginSub(credentialRequest.getRepresentedBySub());
                  
          representedByPerson = optReprPerson.orElseGet(() -> {
              Person reprPerson = new Person();
              reprPerson.setName(credentialRequest.getRepresentedByName());

              AuthService as = new AuthService();
              as.setPerson(reprPerson);
              as.setServer(AcessoCidadaoService.SERVER);
              as.setServerId(credentialRequest.getRepresentedBySub());

              reprPerson.addAuthService(as);

              return personService.save(reprPerson, true);
          });

          representedByPerson.setContactEmail(credentialRequest.getRepresentedByEmail());

        }
        SelfDeclaration sfd = selfDeclarationService.findByPersonAndConference(representedByPerson.getId(), meeting.getConference().getId());

          Optional.ofNullable(sfd).ifPresentOrElse(sf -> {
              selfDeclarationService.updateLocality(sf, credentialRequest.getLocalityId());
          }, 
          () -> {
              representedByPerson.addSelfDeclaration(
                      selfDeclarationService.save(new SelfDeclaration(meeting.getConference(), locality, representedByPerson))
              );
          });

          PreRegistration preRegistration = preRegistrationService.findByMeetingAndPerson(meeting.getId(), representedByPerson.getId());

          preRegistration = Optional.ofNullable(preRegistration)
                                      .map(pr -> {

                                         pr.setPreRegistration(new Date());
                                         pr.setOrganizationGuid(credentialRequest.getOrganization().getGuid());
                                         pr.setOrganization(credentialRequest.getOrganization().getName());
                                         pr.setOrganizationShort(credentialRequest.getOrganization().getShortName());
                                         pr.setRole(credentialRequest.getRole());
                                         pr.setIsTeam(credentialRequest.getIsTeam());
                                         pr.setMadeBy(madeByPerson);

                                          return pr;
                                      })
                                      .orElse(new PreRegistration(
                                          meeting, madeByPerson, representedByPerson, credentialRequest.getOrganization().getGuid(),
                                          credentialRequest.getOrganization().getName(), credentialRequest.getOrganization().getShortName(), credentialRequest.getRole(),
                                          credentialRequest.getIsTeam()));



        PreRegistration savedPreRegistration = preRegistrationService.save(preRegistration, true);
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
  
    @DeleteMapping
    public void deleteCredencial(
            @RequestBody AuthorityCredentialRequest request
    ) {
        
        Meeting meeting = meetingService.find(request.getMeetingId());
        
        Person madeByPerson = personService.find(request.getMadeBy());
        
        Person representedByPerson;
        if(request.getRepresentedByCpf() == null) {
          representedByPerson = madeByPerson;
        } else {

          Optional<Person> optReprPerson = personService.getBySubEmail(request.getRepresentedBySub(), request.getRepresentedByEmail());

          representedByPerson = optReprPerson.orElseGet(() -> {
              Person reprPerson = new Person();
              reprPerson.setContactEmail(request.getRepresentedByEmail());
              reprPerson.setName(request.getRepresentedByName());

              AuthService as = new AuthService();
              as.setPerson(reprPerson);
              as.setServer(AcessoCidadaoService.SERVER);
              as.setServerId(request.getRepresentedBySub());

              reprPerson.addAuthService(as);

              return personService.save(reprPerson, true);
          });


        }
        
        Logger.getGlobal().log(Level.INFO, "meeting id: {0}; representedByPerson id: {1}", new Object[]{meeting.getId(), representedByPerson.getId()});
        
        PreRegistration preRegistration = preRegistrationService.findByMeetingAndPerson(meeting.getId(), representedByPerson.getId());
        
        this.preRegistrationService.deletePreRegistration(preRegistration);
    }
            

    @PutMapping("{idCheckedIn}/toggleAnnounced")
    public ResponseEntity<?> toggleAnnounced(
            @PathVariable Long idCheckedIn
    ) {
        
        return ResponseEntity.of(
                authcSrv
                .toggleAnnounced(idCheckedIn)
                .map(CheckedInAtDto::new)
        );
        
    }

    @PutMapping("{idCheckedIn}/toggleToAnnounce")
    public ResponseEntity<?> toggleToAnnounce(
            @PathVariable Long idCheckedIn
    ) {
        
        return ResponseEntity.of(
                authcSrv
                .toggleToAnnounce(idCheckedIn)
                .map(CheckedInAtDto::new)
        );
        
    }

}
