package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.*;
import br.gov.es.participe.model.CheckedInAt;
import br.gov.es.participe.model.Meeting;
import br.gov.es.participe.model.Person;
import br.gov.es.participe.model.PreRegistration;
import br.gov.es.participe.service.EmailService;
import br.gov.es.participe.service.MeetingService;
import br.gov.es.participe.service.PersonService;
import br.gov.es.participe.service.PreRegistrationService;
import br.gov.es.participe.service.QRCodeService;

import br.gov.es.participe.util.interfaces.ApiPageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.logging.Level;
import java.util.logging.Logger;
/* Início das importações */
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ContentDisposition;
/* Fim das importações */
import javax.imageio.ImageIO;
import javax.mail.MessagingException;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping(value = "/pre-registration")
public class PreRegistrationController {
    
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

    @GetMapping("/{idPreregistration}")
    public ResponseEntity<PreRegistrationDto> getPreregistration(
            @PathVariable Long idPreregistration
    ) {
        
        return ResponseEntity.of(Optional.ofNullable(preRegistrationService.find(idPreregistration))
                                    .map(PreRegistrationDto::new));
        
        
        
    }
    
    @Transactional
    @PostMapping()
    public ResponseEntity<PreRegistrationDto> meetPreRegistration(
        @RequestBody PreRegistrationParamDto preRegistationDto) throws WriterException, IOException, MessagingException {
 
      Person person = personService.find(preRegistationDto.getPersonId());
      Meeting meeting = meetingService.find(preRegistationDto.getMeetingId());
      PreRegistration preRegistration = new PreRegistration(meeting, person);
      PreRegistration savedPreRegistration = preRegistrationService.save(preRegistration);
      byte[] imageQR = qrCodeService.generateQRCode(savedPreRegistration.getId().toString(), 300, 300);

      Map<String, String> emailBody = preRegistrationService.buildEmailBody(meeting);
      String to = person.getContactEmail();
      String title =  meeting.getConference().getName()+" - Pré-Credenciamento";
      emailService.sendEmailPreRegistration(to, title, emailBody, imageQR);


      return ResponseEntity.status(200).body(new PreRegistrationDto(savedPreRegistration,imageQR) );
    }

    @Transactional
    @PostMapping("accreditation")
    public ResponseEntity<AccreditationDto> meetAccreditation(
        @RequestBody AcreditationParamDto accreditationDto) throws WriterException, IOException, MessagingException {
 
      Person person = personService.find(accreditationDto.getPersonId());
      Meeting meeting = meetingService.find(accreditationDto.getMeetingId());
      String persoIdString = "PersonId:" + person.getId().toString();
      byte[] imageQR = qrCodeService.generateQRCode(persoIdString, 300, 300);

      return ResponseEntity.status(200).body(new AccreditationDto(person, meeting, imageQR) );
    }

    @Transactional
    @PostMapping("check-in")
    public ResponseEntity<CheckedInAtDto> preRegistrationCheckin(
      @RequestBody CheckInPreRegistrationParamDto checkInPreRegistationDto){
      PreRegistration preRegistration = preRegistrationService.find(checkInPreRegistationDto.getPreRegistrationId());
      Meeting meeting = meetingService.find(checkInPreRegistationDto.getMeetingId()); 
      CheckedInAt checkedInAt = meetingService.checkInOnMeeting(preRegistration.getPerson().getId(), meeting.getId(),null,null,null,null);
      if (checkedInAt != null) {
        return ResponseEntity.ok().body(new CheckedInAtDto(checkedInAt));
      }
      return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("accreditation-check-in")
    public ResponseEntity<CheckedInAtDto> accreditationCheckin(
      @RequestBody AcreditationParamDto checkInAccreditationDto){
      Person person = personService.find(checkInAccreditationDto.getPersonId());
      Meeting meeting = meetingService.find(checkInAccreditationDto.getMeetingId()); 
      CheckedInAt checkedInAt = meetingService.checkInOnMeeting(person.getId(), meeting.getId(),null,null,null,null);
      if (checkedInAt != null) {
        return ResponseEntity.ok().body(new CheckedInAtDto(checkedInAt));
      }
      return ResponseEntity.noContent().build();
    }

    @GetMapping("/{meetingId}/{personId}") 
    public ResponseEntity getPreRegistration( @PathVariable Long meetingId, @PathVariable Long personId) throws WriterException, IOException, MessagingException  { 
      return ResponseEntity.ok().body(preRegistrationService.getPreRegistrationByMeetingAndPerson(meetingId, personId));
    }

}
